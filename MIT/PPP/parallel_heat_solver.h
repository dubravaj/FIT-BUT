/**
 * @file    parallel_heat_solver.h
 * @author  xdubra03 <xdubra03@stud.fit.vutbr.cz>
 *
 * @brief   Course: PPP 2019/2020 - Project 1
 *
 * @date    2020-MM-DD
 */

#ifndef PARALLEL_HEAT_SOLVER_H
#define PARALLEL_HEAT_SOLVER_H

#include "base_heat_solver.h"

/**
 * @brief The ParallelHeatSolver class implements parallel MPI based heat
 *        equation solver in 2D using 1D and 2D block grid decomposition.
 */
class ParallelHeatSolver : public BaseHeatSolver
{
    //============================================================================//
    //                            *** BEGIN: NOTE ***
    //
    // Modify this class declaration as needed.
    // This class needs to provide at least:
    // - Constructor which passes SimulationProperties and MaterialProperties
    //   to the base class. (see below)
    // - Implementation of RunSolver method. (see below)
    // 
    // It is strongly encouraged to define methods and member variables to improve 
    // readability of your code!
    //
    //                             *** END: NOTE ***
    //============================================================================//
    
public:
    /**
     * @brief Constructor - Initializes the solver. This should include things like:
     *        - Construct 1D or 2D grid of tiles.
     *        - Create MPI datatypes used in the simulation.
     *        - Open SEQUENTIAL or PARALLEL HDF5 file.
     *        - Allocate data for local tile.
     *        - Initialize persistent communications?
     *
     * @param simulationProps Parameters of simulation - passed into base class.
     * @param materialProps   Parameters of material - passed into base class.
     */
    ParallelHeatSolver(SimulationProperties &simulationProps, MaterialProperties &materialProps);
    virtual ~ParallelHeatSolver();

    /**
     * @brief Run main simulation loop.
     * @param outResult Output array which is to be filled with computed temperature values.
     *                  The vector is pre-allocated and its size is given by dimensions
     *                  of the input file (edgeSize*edgeSize).
     *                  NOTE: The vector is allocated (and should be used) *ONLY*
     *                        by master process (rank 0 in MPI_COMM_WORLD)!
     */
    virtual void RunSolver(std::vector<float, AlignedAllocator<float> > &outResult);


protected:

    /**
     * @brief Create datatypes for domain matrix and tile matrix
     */
    void CreateDataTypes();
    /**
     * @brief Splits domain specific parameters (temperature, material params, material type) among all processors
     * @param sendCounts number of sending elements of specific datatype
     * @param displacements offset for data.
     */
    void SplitData(int *sendCounts, int *displacements);
    /**
     * @brief Creates output HDF5 file
     * @param simulationProps simulation properties
     */
    void CreateOutputFile(SimulationProperties &simulationProps);
    /**
     * @brief Send float data to north neighbour
     * @param arr data
     * @param offset offset for data in arr
     * @param dataType specific datatype of sending data
     * @param window MPI Window object in which we are sending data
     */
    void SendToNorth(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    /**
     * @brief Send float data to south neighbour
     * @param arr data
     * @param offset offset for data in arr
     * @param dataType specific datatype of sending data
     * @param window MPI Window object in which we are sending data
     */

    void SendToSouth(float  *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    /**
     * @brief Send float data to west neighbour
     * @param arr data
     * @param offset offset for data in arr
     * @param dataType specific datatype of sending data
     * @param window MPI Window object in which we are sending data
     */

    void SendToWest(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    /**
     * @brief Send float data to east neighbour
     * @param arr data
     * @param offset offset for data in arr
     * @param dataType specific datatype of sending data
     * @param window MPI Window object in which we are sending data
     */

    void SendToEast(float *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    /**
     * @brief Send float data to north neighbour
     * @param arr data
     * @param offset offset for data in arr
     * @param dataType specific datatype of sending data
     * @param window MPI Window object in which we are sending data
     */

    void SendToNorthInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    void SendToSouthInt(int  *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    void SendToWestInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);
    void SendToEastInt(int *arr, int offset, MPI_Datatype &dataType, MPI_Win &window);

    /**
     * @brief Save data using HDF5 in parallel
     * @param fileHandle output file handler
     * @param data input tile data
     * @param iteration number of current iteration
     */
    void StoreDataToFileParallel(hid_t fileHandle, float *data, size_t iteration);
    /**
     * @brief Get number of processor which cover middle column
     * @return number of processors covering middle column
     */
    int  GetProcCountInMidCol();
    /**
     * @brief Compute averarage temperature in middle column of the domain
     * @param data middle column temperature data
     * @return average temperature in domain's middle column
     */
    float ComputeMiddleColTemperature(float *data);
    /**
     * @brief Create datatypes for row halo and column halo
     */
    void CreateHalosDatatype();

    /**
     * @brief Deallocate all resources
     */
    void DeallocateResources();



    MPI_Comm m_cartComm; /// grid communicator
    MPI_Comm m_middleColComm; /// communicator for middle column processes
    MPI_Group worldGroup;
    MPI_Group middleColRanksGroup;


    int m_rank;     ///< Process rank in global (MPI_COMM_WORLD) communicator.
    int m_size;     ///< Total number of processes in MPI_COMM_WORLD.
    int m_midColRank = -1;
    int midColSize = -1;

    int neighbourWest;  /// rank of west neighbour
    int neighbourEast;  /// rank of east neighbour
    int neighbourNorth; /// rank of north neighbour
    int neighbourSouth; /// rank of south neighbour

    int rowShift = 0;
    int colShift = 1;


    AutoHandle<hid_t> m_fileHandle; /// output HDF5 file
    int m_tilesX;    ///number of tiles in X dimension (height)
    int m_tilesY;    ///number of tiles in Y dimension (width)

    int m_tileCols; /// grid size in x
    int m_tileRows; /// grid size in y
    int HALO_SIZE = 4; ///size of halo zone, 2 points from each side
    size_t domainSize; /// domain size

    int myCol;
    int myRow;
    hsize_t offset[2];

    MPI_Datatype m_domainFarmerMatrix;  /// domain grid datatype
    MPI_Datatype m_processTile;         /// process grid datatype
    MPI_Datatype m_northSouthType,m_eastWestType;  /// halos datatypes

    MPI_Datatype m_eastTypeSender, m_westTypeSender; /// halos datatypes
    MPI_Datatype m_eastTypeReceiver, m_eastTypeReceiverNoOffset, m_westTypeReceiver, m_westTypeReceiverNoOffset;


    float *m_tileTemperatureWindowMem; /// tile temperature widow buffer
    float *m_tileDomainParamsMem;      /// tile domain parameters window buffer
    int *m_tileDomainMaterialMapMem;   /// tile domain material map window buffer
    MPI_Win m_temperatureWindow;                  /// tile temperature window
    MPI_Win m_domainParamsWindow;      /// tile domain parameters window
    MPI_Win m_domainMaterialMapWindow; /// tile domain material map window


    std::vector<float, AlignedAllocator<float>> m_tileDataActual;
    std::vector<float, AlignedAllocator<float>> m_tileDataOld;
    std::vector<float, AlignedAllocator<float>> m_domainTemperatureParams;
    std::vector<int, AlignedAllocator<int>> m_domainMaterialMap;

    std::vector<float, AlignedAllocator<float>> m_domainTemperatureParamsTmp;
    std::vector<int, AlignedAllocator<int>> m_domainMaterialMapTmp;
    std::vector<float, AlignedAllocator<float>> m_tmpArray;


};

#endif // PARALLEL_HEAT_SOLVER_H
