export interface ProteinMutation {
  mutationId: string,
  mutation: string,
  protein: string,
  position: number,
  mutatedAminoAcid?:string,
  originalAminoAcid?:string,
  experimentId: string;
  scanRate: string;
  proteinConcentration: string;
  purityOfSample: string;
  method: string;
  methodDetails: string;
  technique: string;
  techniqueDetails: string;
  notes: string;
  ddG: string;
  tm: string;
  cp: string;
  halfLife: string;
  tOffset: string;
  pH: string;
  dTm: string;
  currated: boolean;
  publication: any;
}

export interface PublicationFeatures {
  doi: string;
  title: string;
  journal: string;
  volume: string;
  issue: string;
  year: string;
  pages: string;
  pmid: string;
  authors: any[];
}

export interface ProteinStructure {
  pdbId: string;
  unitNumber: string;
  newChain: string;
  oldChain: string;
  bioUnitId: string;
  method: string;
  resolution: string;
  hswJobs: any[];
}

export interface StructureCoverageData {
  accesion: string;
  start: number;
  end: number;
  color: string;
  shape: string;
  pdbId: string;
}

export interface ResiduesConservation {
  accesion: string;
  start: number;
  end:number;
  color: string;
  shape: string;
  conservation: string;

}

export interface TrackMutations {
  accesion: string;
  start: number;
  end:number;
  color: string;
  shape: string;
  mutation: any[];
}

export interface TrackBFactors {
  accesion: string;
  start: number;
  end:number;
  color: string;
  shape: string;
  bfactors: any[];
}

export interface TrackCatalyticPockets{
  accesion: string;
  start: number;
  color: string;
  shape: string;
  catalyticPockets: any[];
}

export interface TrackProteinTunnels{
  accesion:string;
  start:number;
  color:string;
  shape:string;
  tunnels: any[];
}

export interface TrackBackToConsensus{
  accesion:string;
  start:number;
  end:number;
  color: string;
  shape: string;
  btcData: any[]
}



export interface InterproEntry {
  name: string;
  type: string;
  id: string;
  order: number;
}

export interface ObsoleteUniprotEntry{
  uniprotId:string;
  type:string;
}

export interface CatalyticPocket{
  relevance: number;
  volume: number;
  drugability: number;
  tunnels: any[];
  residuesPositions:number[];
}

export interface ProteinTunnel{
  length:number;
  distanceToSurface:number;
  curvature:number;
  throughput:number;
  priority:number;
  xStart:number;
  yStart:number;
  zStart:number;
  residuesPositions:number[];
}

export interface BtcAnnotation{
    residue: string;
    frequency: number;
    ratio: number;
}


export interface StructuralInfo {
  catalyticPocketResidues: CatalyticPocket[],
  residuesConservation: Map<number,number>,
  residuesBFactors:Map<number, number[]>,
  tunnels: ProteinTunnel[],
  btcAnnotations: Map<string,BtcAnnotation[]>,
  structureIndexesMap: Map<number,number>;
  referenceStructure:string;
  chain:string;

}

export interface StructureMapping {
    structureIndex: number,
    chain: string
}


export interface ProteinDTO {
    sequence:string,
    uniprotId:string,
    sequenceId:number,
    proteinName:string,
    genus:string,
    species:string,
    ecNumber:string,
    proteinFamily:string,
    scientificName:string,
    mutations:ProteinMutation[],
    bioUnits:any,
    structuralInfo:StructuralInfo,
    interproEntries:InterproEntry[],
    obsoleteUniprotId:ObsoleteUniprotEntry

}



export enum AminoAcids{
    A,
    R,
    N,
    D,
    C,
    Q,
    E,
    G,
    H,
    I,
    L,
    K,
    M,
    F,
    P,
    S,
    T,
    W,
    Y,
    V,
    Z
}

export interface MolstarEntry {
  struct_asym_id: string,
  start_residue_number: number,
  end_residue_number: number,
  color: { r: number, g: number, b: number},
  focus: boolean,
  sideChain: boolean
}

export interface SliderValue {
  sliderMin:number,
  sliderMax:number,
  option:string
}


interface ProteinState {

  publications: PublicationFeatures[];
  uniprotId: string,
  sequenceId: number,
  organism: string,
  proteinName: string,
  genus: string,
  species: string,
  proteinFamily: string,
  ecNumber: string,
  sequence: string,
  interproEntries: InterproEntry[],
  mutations: ProteinMutation[],
  visibleMutations: MolstarEntry[],
  pdbStructures: ProteinStructure[],
  currentBioUnit: any,
  pdbStructuresVisibility: any[],
  mutationsPositions: number[],
  catalyticPockets: CatalyticPocket[];
  proteinTunnels:ProteinTunnel[];
  loading: boolean;
  dataSize: number;
  residuesConservation: ResiduesConservation[];
  trackMutations: TrackMutations[];
  trackBFactors: TrackBFactors[];
  trackCatalyticPockets: TrackCatalyticPockets[];
  trackProteinTunnels:TrackProteinTunnels[];
  trackBackToConsensus: TrackBackToConsensus[];
  notFound: boolean;
  obsoleteUniprot: any;
  mutationsDdgMap:Map<number,number[]>;
  mutationsDtmMap:Map<number,number[]>;
  conservationMap:Map<number,number>;
  bfactorMap:Map<number,number>;
  mutationsAAMap:Map<number,string[]>;
  structuralMappingMap:Map<number, number>,
  sliderCurrentOption:any;
  structureShownProtvistaMutation:number;
  structureShowProtvistaMutation:boolean;
  structureViewerInstance:any;
  referenceStructure:string;
  chain:string;
  sliderValue: SliderValue

}

export default ProteinState;
