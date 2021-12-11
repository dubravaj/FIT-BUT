import React, { Component } from "react";
import { ProteinBioUnitsPropsData, ProteinBioUnitsPropsFunctions } from "../../../../interface/props/ProteinBioUnitsProps";
import { withStyles } from "@material-ui/core/styles";
import styles from '../../../../styles/proteinStyle';
import Select from "react-select-virtualized";
import HswTooltip from "../../../tooltips/HswTooltipComponent";
import 'rc-slider/assets/index.css';
import { StabilitySliderContainer, BFactorSliderContainer, ConservationSliderContainer } from "../../../../containers/protein/ProteinBioUnitsContainer";
import Button from "@material-ui/core/Button";

class ProteinBioUnitsComponent extends Component<ProteinBioUnitsPropsData & ProteinBioUnitsPropsFunctions> {

  state: {
    currentSliderValue: number,
    mutationPositions: number[],
    currentSliderRange: any,
    currentMax: any,
    currentMin: any,
    structureOptions: any[]
  } = {
      currentSliderValue: 0,
      mutationPositions: [],
      currentSliderRange: [0, 0],
      currentMin: 0,
      currentMax: 0,
      structureOptions: []
    }

  sliderOptions: any[] = [{ value: "ddG", label: "ΔΔG" }, { value: "dTm", label: "ΔTm" }, { value: "conservation", label: "Conservation" }, { value: "bfactor", label: "B-factor" }];
  infoMsg: string = "Info: Residues can be displayed only in the reference structure. If you want to display residue using slider or from mutations table or ProtVista track, please select reference structure."
  searchSliderMsg: string = "Info: Search is performed based on the name of currently viewed protein and range of values selected using slider."

  stylesStructureSelect: any = {
    menu: (base) => ({
      ...base,
      width: 160
    }),
    control: (base) => ({
      ...base,
      minHeight: 28,
      height: 30,
      fontSize: 13,
      width: 170
    }),
    dropdownIndicator: (base) => ({
      ...base,
      paddingTop: 0,
      paddingBottom: 0,
    }),
    clearIndicator: (base) => ({
      ...base,
      width: 0,
      height: 0,
      paddingTop: 0,
      paddingBottom: 0,
    }),
  };

  stylesSliderSelect: any = {

    control: (base) => ({
      ...base,
      minHeight: 28,
      height: 30,
      width: 120
    }),
    dropdownIndicator: (base) => ({
      ...base,
      paddingTop: 0,
      paddingBottom: 0,
    }),
    clearIndicator: (base) => ({
      ...base,
      visibility: "hidden",
      width: 0,
      height: 0,
      paddingTop: 0,
      paddingBottom: 0,
    }),
  }

  componentDidMount() {

    // set pdbe molstar viewer instance reference to be used across protein page components
    var pdbeMolstar: any = document.getElementById(`molstar-viewer`)!;
    this.props.setStructureViewerInstance(pdbeMolstar.viewerInstance);
    
    // set options for currently displayed structure select
    let options: any[] = [];
    this.props.pdbStructures.forEach((structure, index) => {
      options.push({ value: structure.pdbId, label: structure.pdbId === this.props.referenceStructure ? structure.pdbId + " (reference)" : structure.pdbId });
    })
    this.setState({ structureOptions: options });
  }

  updateSliderRange(value) {
    this.setState({ currentSliderRange: value })
  }

  // render slider component according to selected option
  slider = (option: string) => {
    switch (option) {
      case "ddG":
      case "dTm":
        return <StabilitySliderContainer option={option} />
      case "conservation":
        return <ConservationSliderContainer />
      case "bfactor":
        return <BFactorSliderContainer />
      default:
        return null;
    }

  }

  render() {
    return (
      <div className="fpdb-container protein-bio-units">
        <div className="bio-units-label">Structural features</div>
        <div className="bio-unit-select">
          <label className="label">Select structure:</label>
          <div className="select-container">
            <Select
              styles={this.stylesStructureSelect}
              placeholder=""
              closeMenuOnSelect={true}
              options={this.state.structureOptions}
              value={{ value: this.props.currentBioUnit.pdbId, label: (this.props.currentBioUnit.pdbId === this.props.referenceStructure) ? this.props.currentBioUnit.pdbId + " (reference)" : this.props.currentBioUnit.pdbId }}
              onChange={(unit) => {
                if (unit === null) {
                  this.props.setCurrentBioUnit(this.props.pdbStructures.filter((val) => val.pdbId === this.props.referenceStructure)[0]);
                  return;
                }
                this.props.setCurrentBioUnit(this.props.pdbStructures.filter((val) => val.pdbId === unit.value)[0]);
              }}
            />
          </div>
          <label title={this.infoMsg}><i className="fa fa-info-circle " style={{ marginLeft: "80px" }}></i></label>
          <label title={"Reset reference structure"} onClick={() => {
            this.props.setCurrentBioUnit(this.props.pdbStructures.filter((val) => val.pdbId === this.props.referenceStructure)[0]);
          }}>
            <i className="fa fa-redo" style={{ marginLeft: "10px" }}></i></label>
        </div>
        {
          <div key={this.props.currentBioUnit.pdbId}>
            <div className="pdbId">
              <label className="name"> Structure: </label>
              <label className="value">
                <a target="_blank" rel="noopener noreferrer" href={`https://www.rcsb.org/structure/${this.props.currentBioUnit.pdbId}`}>
                  {this.props.currentBioUnit.pdbId + (this.props.currentBioUnit.pdbId === this.props.referenceStructure ? " (reference)" : "")}
                </a>
              </label>
              {(this.props.currentBioUnit.pdbId === this.props.referenceStructure) ? <HswTooltip bioUnit={this.props.currentBioUnit} /> : null}
            </div>
            <div className="method">
              <label className="name"> Method: </label> <label className="value">{this.props.currentBioUnit.method}</label>
            </div>
            <div className="resolution">
              <label className="name"> Resolution: </label> <label className="value">{this.props.currentBioUnit.resolution + " \u212B"}</label>
            </div>
            <div className="molstar-viewer" id="molstar-viewer-container">
              <pdbe-molstar id={"molstar-viewer"} molecule-id={this.props.currentBioUnit.pdbId.toLowerCase()}
                hide-controls="true" subscribe-events="true"></pdbe-molstar>
            </div>
          </div>
        }
        <div className="structure-ddg-slider">
          <label className="label">Select slider property: </label>
          <div className="selector">
            <Select
              styles={this.stylesSliderSelect}
              placeholder=""
              closeMenuOnSelect={true}
              options={this.sliderOptions}
              value={this.props.sliderCurrentOption}
              onChange={(option) => {
                this.props.setSliderCurrentOption(option);
                this.props.structureViewerInstance!.visual.clearSelection();
                this.props.structureViewerInstance!.visual.reset({ camera: true, theme: true });
              }}
              isDisabled={this.props.currentBioUnit.pdbId !== this.props.referenceStructure ? true : false}
            />
          </div>
          {
            this.slider(this.props.sliderCurrentOption.value)
          }
        </div>
        <div style={{ height: "40px" }}>
          <div className="slider-reset-btn">
            <Button
              className={this.props.classes.sliderResetBtn}
              variant="text"
              onClick={() => {
                this.props.resetVisibleMutations()
              }}
            >
              Reset selection
            </Button>
          </div>
          <div className="slider-search-btn">
            <Button
              className={this.props.classes.sliderSearchBtn}
              variant="text"
              onClick={() => this.props.sliderSearch(this.props.sliderValue, this.props.proteinName, { filterKey: "ddG", order: "asc" }, this.props.history, 0, 20)}>
              {"Search"}
            </Button>
            <label title={this.searchSliderMsg}><i className="fa fa-info-circle " style={{ marginLeft: "10px", marginTop: "5px" }}></i></label>
          </div>
        </div>
      </div>
    )
  }
};
export default withStyles(styles)(ProteinBioUnitsComponent);
