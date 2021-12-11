/**
 * @file    parallel_heat_solver.cpp
 * @author  xdubra03 <xdubra03@stud.fit.vutbr.cz>
 *
 * @brief   Course: PPP 2019/2020 - Project 1
 *
 * @date    2020-MM-DD
 */

#include "parallel_heat_solver.h"

using namespace std;

//============================================================================//
//                            *** BEGIN: NOTE ***
//
// Implement methods of your ParallelHeatSolver class here.
// Freely modify any existing code in ***THIS FILE*** as only stubs are provided 
// to allow code to compile.
//
//                             *** END: NOTE ***
//============================================================================//

ParallelHeatSolver::ParallelHeatSolver(SimulationProperties &simulationProps,
                                       MaterialProperties &materialProps)
    :BaseHeatSolver (simulationProps, materialProps),
     m_fileHandle(H5I_INVALID_HID, static_cast<void (*)(hid_t )>(nullptr)),
    m_tmpArray(materialProps.GetGridPoints())
   {

    MPI_Comm_size(MPI_COMM_WORLD, &m_size);
    MPI_Comm_rank(MPI_COMM_WORLD, &m_rank);

    domainSize = m_materialProperties.GetEdgeSize();

     m_simulationProperties.GetDecompGrid(m_tilesX, m_tilesY);

     /// create grid communicator
     int dims[2] = {m_tilesX, m_tilesY};
     int periods[2] = {0,0};
     int reorder = 0;
     MPI_Cart_create(MPI_COMM_WORLD,2,dims,periods,reorder,&m_cartComm);

     /// compute ranks of neighbours in each direction, -2 means no neighbour
     MPI_Cart_shift(m_cartComm, rowShift , 1, &neighbourNorth, &neighbourSouth);
     MPI_Cart_shift(m_cartComm, colShift , 1, &neighbourWest, &neighbourEast);

     /// compute size of grid for 1 processor, width and height
     m_tileCols = m_materialProperties.GetEdgeSize() / m_tilesY;
     m_tileRows = m_materialProperties.GetEdgeSize() / m_tilesX;


     int coords[2];
     MPI_Cart_coords(m_cartComm, m_rank, 2, coords);
     myRow = coords[0];
     myCol = coords[1];

     offset[0] = myRow *  m_tileRows;
     offset[1] = myCol * m_tileCols;


     /// create communicator with processes which cover middle column
     MPI_Comm_group(MPI_COMM_WORLD, &worldGroup);
     int ranks[GetProcCountInMidCol()];
     int procCount = 0;

     for(int rank=0; rank < m_size; rank++){
         MPI_Cart_coords(m_cartComm, rank, 2, coords);
         if(coords[1] == m_tilesY / 2){
             ranks[procCount] = rank;
             procCount += 1;
           }
     }

     int n = GetProcCountInMidCol();
     MPI_Group_incl(worldGroup,n,ranks, &middleColRanksGroup);
     MPI_Comm_create_group(MPI_COMM_WORLD, middleColRanksGroup, 2, &m_middleColComm);

     if(MPI_COMM_NULL != m_middleColComm){
         MPI_Comm_rank(m_middleColComm, &m_midColRank);
     }

     CreateOutputFile(simulationProps);

   }



void ParallelHeatSolver::CreateOutputFile(SimulationProperties &simulationProps) {


    if(!simulationProps.GetOutputFileName().empty()) {
        /// if parallelIO is not used, only rank 0 will create output file
        if (!simulationProps.IsUseParallelIO()) {
            if (m_rank == 0) {
                m_fileHandle.Set(H5Fcreate(simulationProps.GetOutputFileName("par").c_str(),
                                           H5F_ACC_TRUNC, H5P_DEFAULT, H5P_DEFAULT), H5Fclose);
            }
        }
        else {
            ///parallelIO is specified, create file for parallel usage
                AutoHandle<hid_t> plist_id(H5Pcreate(H5P_FILE_ACCESS), H5Pclose);
                H5Pset_fapl_mpio(plist_id, m_cartComm, MPI_INFO_NULL);
                m_fileHandle.Set(H5Fcreate(simulationProps.GetOutputFileName("par").c_str(), H5F_ACC_TRUNC, H5P_DEFAULT,
                                           plist_id), H5Fclose);

        }
    }
}



ParallelHeatSolver::~ParallelHeatSolver()
{

}

void ParallelHeatSolver::CreateDataTypes(){

    int dims = 2;

    if(m_rank == 0) {
        /// create type for tile without halos from original domain grid
        int domainSize[2] = {m_materialProperties.GetEdgeSize(), m_materialProperties.GetEdgeSize()};
        int tileSize[2] = {m_tileRows, m_tileCols};
        int tileStart[2] = {0, 0};
        MPI_Datatype domainFarmerMatrixTmp;
        MPI_Type_create_subarray(dims, domainSize, tileSize, tileStart, MPI_ORDER_C, MPI_FLOAT, &domainFarmerMatrixTmp);
        MPI_Type_commit(&domainFarmerMatrixTmp);
        MPI_Type_create_resized(domainFarmerMatrixTmp, 0, 1 * sizeof(float), &m_domainFarmerMatrix);
        MPI_Type_commit(&m_domainFarmerMatrix);
   }

    int domainSizeWorker[2] = {m_tileRows + HALO_SIZE, m_tileCols + HALO_SIZE};
    int tileSizeWorker[2] = {m_tileRows, m_tileCols};
    int tileStartWorker[2] = {0,0};
    MPI_Datatype processTileTmp;
    MPI_Type_create_subarray(dims, domainSizeWorker, tileSizeWorker, tileStartWorker, MPI_ORDER_C, MPI_FLOAT, &processTileTmp);
    MPI_Type_create_resized(processTileTmp, 0, 1 * sizeof(float), &m_processTile);
    MPI_Type_commit(&m_processTile);


}

void ParallelHeatSolver::SplitData(int *sendCounts, int *displacements){

    /// send domain temperature parameters
    MPI_Scatterv(m_materialProperties.GetDomainParams().data(), sendCounts, displacements,
                 m_domainFarmerMatrix, &m_domainTemperatureParams[2 * (m_tileCols + HALO_SIZE) + HALO_SIZE / 2], 1,
                 m_processTile, 0, m_cartComm);

    /// send material types
    MPI_Scatterv(m_materialProperties.GetDomainMap().data(), sendCounts, displacements,
                 m_domainFarmerMatrix, &m_domainMaterialMap[2 * (m_tileCols + HALO_SIZE) + HALO_SIZE / 2], 1 ,
                 m_processTile, 0 , m_cartComm);

    /// send initial temperatures
    MPI_Scatterv(m_materialProperties.GetInitTemp().data(), sendCounts, displacements,
                 m_domainFarmerMatrix, &m_tileDataOld[2 * (m_tileCols + HALO_SIZE) + HALO_SIZE / 2], 1 ,
                 m_processTile, 0, m_cartComm);


    std::copy(m_tileDataOld.begin(), m_tileDataOld.end(), m_tileDataActual.begin());

}

void ParallelHeatSolver::SendToNorth(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {
    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourNorth >= 0){

        MPI_Put(&arr[ind(2, 2)], 1, dataType, neighbourNorth, ind(2, m_tileRows + 2 ) + offset, 1 , dataType, window);

    }

}

void ParallelHeatSolver::SendToNorthInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {
    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourNorth >= 0){

        MPI_Put(&arr[ind(2, 2)], 1, dataType, neighbourNorth, ind(2, m_tileRows + 2 ) + offset, 1 , dataType, window);

    }

}



void ParallelHeatSolver::SendToSouth(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {

    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

   if(neighbourSouth >= 0) {

       MPI_Put(&arr[ind(2, m_tileRows)], 1, dataType,neighbourSouth, ind(2, 0) + offset, 1, dataType, window);
   }

}

void ParallelHeatSolver::SendToSouthInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {

    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourSouth >= 0) {

        MPI_Put(&arr[ind(2, m_tileRows)], 1, dataType,neighbourSouth, ind(2, 0) + offset, 1, dataType, window);

    }

}



void ParallelHeatSolver::SendToEast(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {
    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourEast >= 0) {


        if(offset > 0){
                MPI_Put(&arr[ind(m_tileCols, 2)], 1, m_eastTypeSender,
                        neighbourEast, ind(0, 0), 1, m_westTypeReceiver, window);
        }
        else{
            MPI_Put(&arr[ind(m_tileCols, 2)], 1, m_eastTypeSender,
                    neighbourEast, ind(0, 0), 1, m_westTypeReceiverNoOffset, window);
        }

    }


}

void ParallelHeatSolver::SendToEastInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {
    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourEast >= 0) {


        if(offset > 0){
            MPI_Put(&arr[ind(m_tileCols, 2)], 1, m_eastTypeSender,
                    neighbourEast, ind(0, 0), 1, m_westTypeReceiver, window);
        }
        else{
            MPI_Put(&arr[ind(m_tileCols, 2)], 1, m_eastTypeSender,
                    neighbourEast, ind(0, 0), 1, m_westTypeReceiverNoOffset, window);
        }
    }

}



void ParallelHeatSolver::SendToWest(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {
    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourWest >= 0) {


       if(offset > 0) {
           MPI_Put(&arr[ind(2, 2)], 1, m_westTypeSender, neighbourWest,
                   ind(0, 0), 1, m_eastTypeReceiver, window);
       }
       else{
           MPI_Put(&arr[ind(2, 2)], 1, m_westTypeSender, neighbourWest,
                   ind(0, 0), 1, m_eastTypeReceiverNoOffset, window);
       }

    }
}

void ParallelHeatSolver::SendToWestInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window) {
    auto ind = [this](int i, int j){return j*(m_tileCols + HALO_SIZE) + i;};

    if(neighbourWest >= 0) {


        if(offset > 0) {
            MPI_Put(&arr[ind(2, 2)], 1, m_westTypeSender, neighbourWest,
                    ind(0, 0), 1, m_eastTypeReceiver, window);
        }
        else{
            MPI_Put(&arr[ind(2, 2)], 1, m_westTypeSender, neighbourWest,
                    ind(0, 0), 1, m_eastTypeReceiverNoOffset, window);
        }
    }
}

void ParallelHeatSolver::CreateHalosDatatype() {

    int dims= 2 ;
    int tileSize[2] = {m_tileRows + HALO_SIZE, m_tileCols + HALO_SIZE};

    int rowHaloWorker[2] = {2, m_tileCols};
    int rowHaloStartWorker[2] = {0,0};
    MPI_Datatype rowHaloTmp;
    MPI_Type_create_subarray(dims, tileSize, rowHaloWorker, rowHaloStartWorker, MPI_ORDER_C, MPI_FLOAT, &rowHaloTmp);
    MPI_Type_create_resized(rowHaloTmp, 0, 1 * sizeof(float), &m_northSouthType);
    MPI_Type_commit(&m_northSouthType);

    int offset = (m_tileRows + HALO_SIZE) * (m_tileCols + HALO_SIZE);



    /// more datatypes for vertical halos because of problem with MPI_Put offset
    /// offset is defined right in the datatype
    int tileSizeVertical[2] = {(m_tileRows + HALO_SIZE) , (m_tileCols + HALO_SIZE)};
    int colHaloWorker[2] = {m_tileRows, 2};
    int colHaloStartWorker[2] = {0,0};
    MPI_Datatype colHaloTmp1;
    MPI_Type_create_subarray(dims, tileSizeVertical, colHaloWorker, colHaloStartWorker, MPI_ORDER_C, MPI_FLOAT, &colHaloTmp1);
    MPI_Type_create_resized(colHaloTmp1, 0, 1 * sizeof(float), &m_eastTypeSender);
    MPI_Type_commit(&m_eastTypeSender);


    int tileSizeVertical2[2] = { 2 * (m_tileRows + HALO_SIZE),  (m_tileCols + HALO_SIZE)};
    int colHaloStartWorker1[2] = {2 + m_tileRows + HALO_SIZE ,0};
    MPI_Datatype colHaloTmp2;
    MPI_Type_create_subarray(dims, tileSizeVertical2, colHaloWorker, colHaloStartWorker1, MPI_ORDER_C, MPI_FLOAT, &colHaloTmp2);
    MPI_Type_create_resized(colHaloTmp2, 0, 1 * sizeof(float), &m_westTypeReceiver);
    MPI_Type_commit(&m_westTypeReceiver);

    MPI_Datatype colHaloTmp2NoOffset;
    int colHaloStartWorker1NoOffset[2] = {2,0};
    MPI_Type_create_subarray(dims, tileSizeVertical2, colHaloWorker, colHaloStartWorker1NoOffset, MPI_ORDER_C, MPI_FLOAT, &colHaloTmp2NoOffset);
    MPI_Type_create_resized(colHaloTmp2NoOffset, 0, 1 * sizeof(float), &m_westTypeReceiverNoOffset);
    MPI_Type_commit(&m_westTypeReceiverNoOffset);



    int colHaloStartWorker2[2] = {0,0};
    MPI_Datatype colHaloTmp3;
    MPI_Type_create_subarray(dims, tileSizeVertical, colHaloWorker, colHaloStartWorker2, MPI_ORDER_C, MPI_FLOAT, &colHaloTmp3);
    MPI_Type_create_resized(colHaloTmp3, 0, 1 * sizeof(float), &m_westTypeSender);
    MPI_Type_commit(&m_westTypeSender);


   int colHaloStartWorker3[2] = {2 + m_tileRows + HALO_SIZE,m_tileCols + 2};
    MPI_Datatype colHaloTmp4;
    MPI_Type_create_subarray(dims, tileSizeVertical2, colHaloWorker, colHaloStartWorker3, MPI_ORDER_C, MPI_FLOAT, &colHaloTmp4);
    MPI_Type_create_resized(colHaloTmp4, 0, 1 * sizeof(float), &m_eastTypeReceiver);
    MPI_Type_commit(&m_eastTypeReceiver);


    MPI_Datatype colHaloTmp4NoOffset;
    int colHaloStartWorker3NoOffset[2] = {2,m_tileCols + 2};
    MPI_Type_create_subarray(dims, tileSizeVertical2, colHaloWorker, colHaloStartWorker3NoOffset, MPI_ORDER_C, MPI_FLOAT, &colHaloTmp4NoOffset);
    MPI_Type_create_resized(colHaloTmp4NoOffset, 0, 1 * sizeof(float), &m_eastTypeReceiverNoOffset);
    MPI_Type_commit(&m_eastTypeReceiverNoOffset);


}



void ParallelHeatSolver::StoreDataToFileParallel(hid_t fileHandle, float *data, size_t iteration){

     hsize_t gridSize[] = { m_materialProperties.GetEdgeSize(), m_materialProperties.GetEdgeSize() };
     hsize_t tileSize[] = {m_tileRows + HALO_SIZE, m_tileCols + HALO_SIZE};
     hsize_t tileCount[] = {m_tileRows, m_tileCols};
     hsize_t offsetData[] = {2,2};
     herr_t status;

     std::string groupName = "Timestep_" + std::to_string(static_cast<unsigned long long>(iteration / m_simulationProperties.GetDiskWriteIntensity()));

     AutoHandle<hid_t> groupHandle(H5Gcreate(fileHandle, groupName.c_str(),
                                             H5P_DEFAULT, H5P_DEFAULT, H5P_DEFAULT), H5Gclose);

    {
        /// create dataset for temperature
        std::string dataSetName("Temperature");
        /// define shape of the dataset for my tile - tileRows + HALO x tileCols + HALO.
        AutoHandle<hid_t> tileDataMemSpaceHandle(H5Screate_simple(2, tileSize, NULL), H5Sclose);
        /// define shape of whole dataset
        AutoHandle<hid_t> dataFileSpaceHandle(H5Screate_simple(2, gridSize, NULL), H5Sclose);


        /// create dataset with specified shape.
        AutoHandle<hid_t> dataSetHandle(H5Dcreate(groupHandle, dataSetName.c_str(),
                                                  H5T_NATIVE_FLOAT, dataFileSpaceHandle,
                                                  H5P_DEFAULT,H5P_DEFAULT, H5P_DEFAULT), H5Dclose);

        /// select space in tile, to get data
        status = H5Sselect_hyperslab(tileDataMemSpaceHandle, H5S_SELECT_SET, offsetData, NULL, tileCount, NULL);

        /// select space in dataset, to save tile data
        status = H5Sselect_hyperslab(dataFileSpaceHandle, H5S_SELECT_SET, offset, NULL, tileCount, NULL);


        AutoHandle<hid_t> plist_id(H5Pcreate(H5P_DATASET_XFER), H5Pclose);
        H5Pset_dxpl_mpio(plist_id, H5FD_MPIO_COLLECTIVE);

        status = H5Dwrite(dataSetHandle, H5T_NATIVE_FLOAT,
                          tileDataMemSpaceHandle, dataFileSpaceHandle, plist_id, data);

    }

    {
        /// write time
        std::string attributeName("Time");

        /// dataspace is single value/scalar.
        AutoHandle<hid_t> dataSpaceHandle(H5Screate(H5S_SCALAR), H5Sclose);

        /// create the attribute in the group as double.
        AutoHandle<hid_t> attributeHandle(H5Acreate2(groupHandle, attributeName.c_str(),
                                                     H5T_IEEE_F64LE, dataSpaceHandle,
                                                     H5P_DEFAULT, H5P_DEFAULT), H5Aclose);

        /// write value into the attribute.
        double snapshotTime = double(iteration);
        H5Awrite(attributeHandle, H5T_IEEE_F64LE, &snapshotTime);
    }

}

float ParallelHeatSolver::ComputeMiddleColTemperature(float *data) {
    float tileMidColAvgTemperature = 0.0f;

    /// check if my rank is in middle column communicator
    if(MPI_COMM_NULL != m_middleColComm){

        for(int i=2; i < m_tileRows + 2; i++){
            if(m_tilesY >= 2) {
                tileMidColAvgTemperature += data[i * (m_tileCols + HALO_SIZE) + (HALO_SIZE / 2)];
            }
            else{
                tileMidColAvgTemperature += data[i * (m_tileCols + HALO_SIZE) + ((HALO_SIZE / 2) + m_tileCols / 2)];

            }
        }

    }

    return tileMidColAvgTemperature;
}


int ParallelHeatSolver::GetProcCountInMidCol(){
    int procCount = 0;
    int coords[2];
    for(int rank=0; rank < m_size; rank++){
        MPI_Cart_coords(m_cartComm, rank, 2, coords);
        if(coords[1] == m_tilesY / 2){
            procCount += 1;
        }
    }

    return procCount;
}

void ParallelHeatSolver::DeallocateResources() {
    MPI_Win_free(&m_temperatureWindow);
    MPI_Win_free(&m_domainMaterialMapWindow);
    MPI_Win_free(&m_domainParamsWindow);

    if(m_rank == 0) {
        MPI_Type_free(&m_domainFarmerMatrix);
        MPI_Type_free(&m_processTile);
    }
    MPI_Type_free(&m_northSouthType);
    MPI_Type_free(&m_eastTypeReceiverNoOffset);
    MPI_Type_free(&m_eastTypeReceiver);
    MPI_Type_free(&m_eastTypeSender);
    MPI_Type_free(&m_westTypeSender);
    MPI_Type_free(&m_westTypeReceiverNoOffset);
    MPI_Type_free(&m_westTypeReceiver);

}


void ParallelHeatSolver::RunSolver(std::vector<float, AlignedAllocator<float> > &outResult) {



    /// create data types for tiles
    CreateDataTypes();

    ///allocate space for tile data for each process
    m_tileDataActual.resize((m_tileRows + HALO_SIZE) * (m_tileCols + HALO_SIZE));
    m_tileDataOld.resize((m_tileRows + HALO_SIZE) * (m_tileCols + HALO_SIZE));
    m_domainTemperatureParams.resize((m_tileRows + HALO_SIZE) * (m_tileCols + HALO_SIZE));
    m_domainMaterialMap.resize((m_tileRows + HALO_SIZE) * (m_tileCols + HALO_SIZE));


    int tileSize = m_tileRows * m_tileCols;
    int *sendCounts = new int[m_size];
    int *displacements = new int[m_size];
    if (m_rank == 0) {

        /// we are sending 1 item of certain type
        for (int i = 0; i < m_size; i++) {
            sendCounts[i] = 1;
        }

        /// compute displacements for each processor tile in processors grid
        for (int i = 0 ; i < m_tilesX; i++)
        {
            for (int j = 0; j < m_tilesY; j++) {
                displacements[i * m_tilesY + j] = (tileSize * m_tilesY * i) + m_tileCols * j;
            }
        }



    }

    /// split data among processes
    SplitData(sendCounts, displacements);


    /// creating shared windows for sending halos
    int size = (m_tileRows + HALO_SIZE) * (m_tileCols + HALO_SIZE);

    MPI_Win_allocate(2 * size * sizeof(float), sizeof(float), MPI_INFO_NULL, m_cartComm, &m_tileTemperatureWindowMem, &m_temperatureWindow);
    MPI_Win_allocate(size * sizeof(float), sizeof(float), MPI_INFO_NULL, m_cartComm, &m_tileDomainParamsMem, &m_domainParamsWindow);
    MPI_Win_allocate(size * sizeof(int), sizeof(int), MPI_INFO_NULL, m_cartComm, &m_tileDomainMaterialMapMem, &m_domainMaterialMapWindow);


    std::copy(m_tileDataActual.begin(), m_tileDataActual.end(), &m_tileTemperatureWindowMem[0]);
    std::copy(m_tileDataOld.begin(), m_tileDataOld.end(), &m_tileTemperatureWindowMem[0] + size);
    std::copy(m_domainMaterialMap.begin(), m_domainMaterialMap.end(), &m_tileDomainMaterialMapMem[0]);
    std::copy(m_domainTemperatureParams.begin(), m_domainTemperatureParams.end(), &m_tileDomainParamsMem[0]);





    /// working arrays
    float *tileNewData = m_tileTemperatureWindowMem;
    float *tileOldData = m_tileTemperatureWindowMem + size;

    /// create datatypes for halo zones
    CreateHalosDatatype();


    /// send initial temperatures
    MPI_Win_fence(0, m_temperatureWindow);
    int offset = size;


    SendToNorth(tileOldData, offset, m_northSouthType, m_temperatureWindow);

    SendToSouth(tileOldData, offset, m_northSouthType, m_temperatureWindow);

    SendToWest(tileOldData, offset, m_eastWestType, m_temperatureWindow);

    SendToEast(tileOldData, offset, m_eastWestType, m_temperatureWindow);

    MPI_Win_fence(0, m_temperatureWindow);



    /// send domain material params
    MPI_Win_fence(0, m_domainMaterialMapWindow);
    offset = 0;

    SendToNorthInt(m_tileDomainMaterialMapMem, offset, m_northSouthType, m_domainMaterialMapWindow);

    SendToSouthInt(m_tileDomainMaterialMapMem, offset, m_northSouthType, m_domainMaterialMapWindow);

    SendToWestInt(m_tileDomainMaterialMapMem, offset, m_eastWestType, m_domainMaterialMapWindow);

    SendToEastInt(m_tileDomainMaterialMapMem, offset, m_eastWestType, m_domainMaterialMapWindow);

    MPI_Win_fence(0, m_domainMaterialMapWindow);

    /// send domain params
    MPI_Win_fence(0, m_domainParamsWindow);
    offset = 0;

    SendToNorth(m_tileDomainParamsMem, offset, m_northSouthType, m_domainParamsWindow);

    SendToSouth(m_tileDomainParamsMem, offset, m_northSouthType, m_domainParamsWindow);

    SendToWest(m_tileDomainParamsMem, offset, m_eastWestType, m_domainParamsWindow);

    SendToEast(m_tileDomainParamsMem, offset, m_eastWestType, m_domainParamsWindow);


    MPI_Win_fence(0, m_domainParamsWindow);


    double simulationStartTime;
    float tileMidColAvgTemperature = 0.0f;
    float middleColAvgTemperature = 0.0f;

    if(m_midColRank == 0) {
        simulationStartTime = MPI_Wtime();
    }



    for(size_t simStep=0; simStep < m_simulationProperties.GetNumIterations(); ++simStep) {

         /* Compute without overlapping

         size_t offsetX = 2;
         size_t offsetY = 2;
         size_t sizeX = m_tileCols;
         size_t sizeY = m_tileRows;
         size_t strideX = m_tileCols + HALO_SIZE;

         if(neighbourNorth < 0){
             offsetY = 4;
             sizeY = m_tileRows - 2;

         }
         if(neighbourSouth < 0){
             sizeY = m_tileRows - 2;
         }


         if(neighbourEast < 0){

             sizeX = m_tileCols - 2;
         }

         if(neighbourWest < 0){
             offsetX = 4;
             sizeX = m_tileCols - 2;
         }

         // 1D - no north and south neighbour
         if(neighbourNorth < 0 && neighbourSouth < 0){
             offsetY = 4;
             sizeY = m_tileRows - 4;
         }

         if(neighbourEast < 0 && neighbourWest < 0){
             offsetX = 4;
             sizeX = m_tileCols - 4;
         }

         // just one processor for whole domain
         if(neighbourNorth < 0 && neighbourSouth < 0
            && neighbourEast < 0 && neighbourWest < 0){
             offsetY = 4;
             sizeY = m_tileRows - 4;
             offsetX = 4;
             sizeX = m_tileCols - 4;
         }


             // update tile data
             UpdateTile(tileOldData,
                        tileNewData,
                        tileDomainParamsMem,
                        tileDomainMaterialMapMem,
                        offsetX, offsetY, sizeX, sizeY, m_tileCols + HALO_SIZE, m_simulationProperties.GetAirFlowRate(),
                        m_materialProperties.GetCoolerTemp());



         MPI_Win_fence(0, m_temperatureWindow);
         int offset = size * (simStep % 2);

         SendToNorth(tileNewData, offset, m_northSouthType, m_temperatureWindow);

         SendToSouth(tileNewData, offset, m_northSouthType, m_temperatureWindow);

         SendToWest(tileNewData, offset, m_eastWestType, m_temperatureWindow);

         SendToEast(tileNewData, offset, m_eastWestType, m_temperatureWindow);

         MPI_Win_fence(0, m_temperatureWindow);*/


        size_t rowStart = (neighbourNorth < 0) ? 4 : 2;
        size_t rowEnd = (neighbourSouth < 0) ? m_tileRows : m_tileRows + 2;
        size_t colStart = (neighbourWest < 0) ? 4 : 2;
        size_t colEnd = (neighbourEast < 0) ? m_tileCols : m_tileCols + 2;



        /// compute north margin
        if (neighbourNorth >= 0) {
            if (neighbourWest >= 0 || neighbourSouth >= 0 || neighbourEast >= 0 || m_tileRows > 2) {
                for (size_t i = 2; i < 4; i++) {
                    for (size_t j = colStart; j < colEnd; j++) {
                        ComputePoint(tileOldData, tileNewData, m_tileDomainParamsMem, m_tileDomainMaterialMapMem,
                                     i, j, m_tileCols + HALO_SIZE, m_simulationProperties.GetAirFlowRate(),
                                     m_materialProperties.GetCoolerTemp());

                    }
                }
            }
        }


        /// compute south margin
        if (neighbourSouth >= 0) {
            if (neighbourNorth >= 0 || neighbourEast >= 0 || neighbourWest >= 0 || m_tileRows > 2) {
                for (size_t i = m_tileRows; i < m_tileRows + 2; i++) {
                    for (size_t j = colStart; j < colEnd; j++) {
                        ComputePoint(tileOldData, tileNewData, m_tileDomainParamsMem, m_tileDomainMaterialMapMem,
                                     i, j, m_tileCols + HALO_SIZE, m_simulationProperties.GetAirFlowRate(),
                                     m_materialProperties.GetCoolerTemp());

                    }
                }
            }
        }

        /// compute east margin
        if (neighbourEast >= 0) {
            if (neighbourNorth >= 0 || neighbourSouth >= 0 || neighbourWest >= 0 || m_tileCols > 2) {
                for (size_t i = rowStart; i < rowEnd; i++) {
                    for (size_t j = m_tileCols; j < m_tileCols + 2; j++) {
                        ComputePoint(tileOldData, tileNewData, m_tileDomainParamsMem, m_tileDomainMaterialMapMem,
                                     i, j, m_tileCols + HALO_SIZE, m_simulationProperties.GetAirFlowRate(),
                                     m_materialProperties.GetCoolerTemp());
                    }
                }
            }

        }

        /// compute west margin
        if (neighbourWest >= 0) {
            if (neighbourEast >= 0 || neighbourSouth >= 0 || neighbourNorth >= 0 || m_tileCols > 2) {
                for (size_t i = rowStart; i < rowEnd; i++) {
                    for (size_t j = 2; j < 4; j++) {
                        ComputePoint(tileOldData, tileNewData, m_tileDomainParamsMem, m_tileDomainMaterialMapMem,
                                     i, j, m_tileCols + HALO_SIZE, m_simulationProperties.GetAirFlowRate(),
                                     m_materialProperties.GetCoolerTemp());
                    }
                }
            }

        }



        /// send margins to neighbours
        MPI_Win_fence(0, m_temperatureWindow);
        int offset = size * (simStep % 2);

        if (neighbourNorth >= 0) {
            SendToNorth(tileNewData, offset, m_northSouthType, m_temperatureWindow);
        }
        if (neighbourSouth >= 0) {
            SendToSouth(tileNewData, offset, m_northSouthType, m_temperatureWindow);
        }
        if (neighbourWest >= 0) {
            SendToWest(tileNewData, offset, m_eastWestType, m_temperatureWindow);
        }
        if (neighbourEast >= 0) {
            SendToEast(tileNewData, offset, m_eastWestType, m_temperatureWindow);
        }


        /// compute remaining part of tile
        size_t offsetX = 4;
        size_t offsetY = 4;
        size_t sizeX = m_tileCols - 4;
        size_t sizeY = m_tileRows - 4;
        size_t strideX = m_tileCols + HALO_SIZE;


        if((m_tileRows > 4 && m_tileCols > 4)) {
            UpdateTile(tileOldData,
                       tileNewData,
                       m_tileDomainParamsMem,
                       m_tileDomainMaterialMapMem,
                       offsetX, offsetY, sizeX, sizeY, m_tileCols + HALO_SIZE, m_simulationProperties.GetAirFlowRate(),
                       m_materialProperties.GetCoolerTemp());


        }



        MPI_Win_fence(0,m_temperatureWindow);


        if(ShouldPrintProgress(simStep)) {
            if (MPI_COMM_NULL != m_middleColComm) {
                tileMidColAvgTemperature = ComputeMiddleColTemperature(tileNewData);
            }

            /// reduce middle col temperature results from middleCol communicator
            if (MPI_COMM_NULL != m_middleColComm) {
                MPI_Reduce(&tileMidColAvgTemperature, &middleColAvgTemperature, 1, MPI_FLOAT, MPI_SUM, 0,
                           m_middleColComm);
            }

            if (m_midColRank == 0) {
                middleColAvgTemperature /= domainSize;
                PrintProgressReport(simStep, middleColAvgTemperature);
            }
        }


        if(simStep % m_simulationProperties.GetDiskWriteIntensity() == 0){

            /// parallel write of simulation state
            if(m_simulationProperties.IsUseParallelIO()) {
                if(m_fileHandle != H5I_INVALID_HID) {
                    StoreDataToFileParallel(m_fileHandle, tileNewData, simStep);
                }
            }
            else{

                MPI_Gatherv(&tileNewData[2*(m_tileCols + HALO_SIZE) + HALO_SIZE / 2] ,1 , m_processTile, outResult.data(), sendCounts, displacements, m_domainFarmerMatrix, 0 , m_cartComm);
                /// simulation state is written to file by master processor
                if(m_fileHandle != H5I_INVALID_HID){
                    if(m_rank == 0) {
                        StoreDataIntoFile(m_fileHandle, simStep, outResult.data());
                    }
                }
            }

        }

        /// swap working arrays
        std::swap(tileOldData, tileNewData);

    }


    /// compute middle column temperature after simulation
    if (MPI_COMM_NULL != m_middleColComm) {
        tileMidColAvgTemperature = ComputeMiddleColTemperature(tileOldData);
    }

    /// reduce middle col temperature results from middleCol communicator
    if (MPI_COMM_NULL != m_middleColComm) {
        MPI_Reduce(&tileMidColAvgTemperature, &middleColAvgTemperature, 1, MPI_FLOAT, MPI_SUM, 0,
                   m_middleColComm);
    }

    if (m_midColRank == 0) {
        middleColAvgTemperature /= domainSize;
    }




    MPI_Gatherv(&tileOldData[2 * (m_tileCols + HALO_SIZE) + HALO_SIZE / 2], 1 , m_processTile, outResult.data(), sendCounts, displacements, m_domainFarmerMatrix, 0 , m_cartComm);


    if(m_midColRank == 0) {
        double simulationTime = MPI_Wtime() - simulationStartTime;
        PrintFinalReport(simulationTime, middleColAvgTemperature, "par");
    }




    DeallocateResources();
    delete [] displacements;
    delete [] sendCounts;

}








