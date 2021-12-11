import * as React from "react";
import { Component } from 'react';
import Slider, { createSliderWithTooltip } from 'rc-slider';
import 'rc-slider/assets/index.css';
import { MolstarEntry } from "../../../../interface/ProteinState";
import {ProteinBioUnitsPropsData, ProteinBioUnitsPropsFunctions} from  "../../../../interface/props/ProteinBioUnitsProps";
import {createMolstarEntry} from "../../../../containers/protein/ProteinMutationsContainer";

const Range = createSliderWithTooltip(Slider.Range);



interface StabilitySliderProps {
    option: string;
}

class StabilitySliderComponent extends Component<StabilitySliderProps & ProteinBioUnitsPropsData & ProteinBioUnitsPropsFunctions>{

    state: {
        currentSliderValue: number,
        mutationPositions: Array<number>,
        thumbClass: any,
        railClass: any,
        currentSliderRange: any,
        currentMax: any,
        currentMin: any,
        disableSlider:boolean
    } = {
            currentSliderValue: 0,
            mutationPositions: [],
            thumbClass: undefined,
            railClass: undefined,
            currentSliderRange: [0, 0],
            currentMin: 0,
            currentMax: 0,
            disableSlider:false
        }

    ddgSliderMinValue: number = 0;
    ddgSliderMaxValue: number = 0;
    dtmSliderMinValue: number = 0;
    dtmSliderMaxValue: number = 0;
    sliderOptions: Array<any> = [{ value: "ddG", label: "ΔΔG" }, { value: "dTm", label: "ΔTm" }];
    sliderDdgMarks: Array<any> = [];
    addOneDdg: boolean = false;
    addMinusOneDdg: boolean = false;
    addOneDtm: boolean = false;
    addMinusOneDtm: boolean = false;
    disableSliderDdg:boolean = Object.keys(this.props.mutationsDdgMap).length === 0;
    disableSliderDtm:boolean = Object.keys(this.props.mutationsDtmMap).length === 0; 
    disableSlider:boolean = false;

    componentDidMount() {
        this.getMutationsDdgDtmRange();
        if(this.props.currentBioUnit.pdbId !== this.props.referenceStructure){
            this.setState({disableSlider:true});
            return;
        }
        else if(this.props.currentBioUnit.pdbId === this.props.referenceStructure){
            this.setState({disableSlider:(this.props.option === "ddG") ? this.disableSliderDdg : this.disableSliderDtm});
            return;
        }
        
    }

    componentDidUpdate(prevProps, prevState) {

        // handle change of currently selected structure and option in slider
        if(prevProps.currentBioUnit.pdbId !== this.props.currentBioUnit.pdbId){
            this.setState({disableSlider: (this.props.currentBioUnit.pdbId === this.props.referenceStructure) ? false : true});
         
        }
        else if(prevProps.option !== this.props.option){
            this.setCurrentRangeLimits(this.props.option);
            this.setState({disableSlider:(this.props.option === "ddG") ? this.disableSliderDdg : this.disableSliderDtm});
        }
    }

    // Compute range of ddG and dTm values in the data
    getMutationsDdgDtmRange() {
        let currentDdgMin: number = 0;
        let currentDdgMax: number = 0;
        let currentDtmMin: number = 0;
        let currentDtmMax: number = 0;
        let ddGValues: number[] = [];
        let dTmValues: number[] = [];

       
        let ddG = Object.values(this.props.mutationsDdgMap);
        let dTm = Object.values(this.props.mutationsDtmMap);
        if(ddG.length){
            ddG.forEach(entry => {
                ddGValues = ddGValues.concat(entry);
            })
            currentDdgMax = Math.max.apply(null, ddGValues);
            currentDdgMin = Math.min.apply(null, ddGValues);
    
            
        }
        if(dTm.length){
            dTm.forEach(entry => {
                dTmValues = dTmValues.concat(entry);
            })

            currentDtmMax = Math.max.apply(null, dTmValues);
            currentDtmMin= Math.min.apply(null, dTmValues);
        }

        this.ddgSliderMinValue = currentDdgMin;
        this.ddgSliderMaxValue = currentDdgMax;
        this.dtmSliderMinValue = currentDtmMin;
        this.dtmSliderMaxValue = currentDtmMax;
        if (this.props.option === 'ddG') {
            this.setState({ currentMax: this.ddgSliderMaxValue, currentMin: this.ddgSliderMinValue });
        }
        else {
            this.setState({ currentMax: this.dtmSliderMaxValue, currentMin: this.dtmSliderMinValue });
        }

        this.props.setSliderValue({
                sliderMin: 0,
                sliderMax: 0,
                option: this.props.option 
           })
          
    
    }

    // Set default range values
    setCurrentRangeLimits(option) {
        if (option === "ddG") {
            this.setState({ currentMax: this.ddgSliderMaxValue, currentMin: this.ddgSliderMinValue, currentSliderRange: [0, 0] })
        }
        else {
            this.setState({ currentMax: this.dtmSliderMaxValue, currentMin: this.dtmSliderMinValue, currentSliderRange: [0, 0] });
        }
    }

    // Set positions of residues that are withing range of ddG values
    async setCurrentDdgPositions(ddgRange: number[]) {

        let positions: number[] = [];
        let molstarDataEntries:MolstarEntry[] = [];

        for (let [key, value] of Object.entries(this.props.mutationsDdgMap)) {
            for (let ddgVal of value) {
                if (ddgVal >= ddgRange[0] && ddgVal <= ddgRange[1]) {
                    let structureIndex:number = this.props.structureMappingMap[Number(key)];
                    if (!positions.includes(structureIndex)) {
                        positions.push(structureIndex);
                        molstarDataEntries.push(createMolstarEntry(true,this.props.chain,structureIndex));
                    }
                }
            }
        }
        this.props.setVisibleMutations(molstarDataEntries);

        if (!positions.length) {
            this.props.structureViewerInstance!.visual.clearSelection();
            return;
        }

        await this.props.structureViewerInstance!.visual.clearSelection();
        await this.props.structureViewerInstance.visual.select({ data: this.props.visibleMutations });

    }

    // Set positions of residues that are within dTm range values
    async setCurrentDtmPositions(dtmRange: number[]) {
        let positions: number[] = [];
        let molstarDataEntries:MolstarEntry[] = [];

        for (let [key, value] of Object.entries(this.props.mutationsDtmMap)) {
            for (let dtmVal of value) {
                if (dtmVal >= dtmRange[0] && dtmVal <= dtmRange[1]) {
                    let structureIndex:number = this.props.structureMappingMap[Number(key)];
                    if (!positions.includes(structureIndex)) {
                        positions.push(structureIndex);
                        molstarDataEntries.push(createMolstarEntry(true,this.props.chain,structureIndex));
                    }
                }
            }
        }

        if (!positions.length) {
            this.props.structureViewerInstance!.visual.clearSelection();
            return;
        }

        this.props.setVisibleMutations(molstarDataEntries);

        await this.props.structureViewerInstance!.visual.clearSelection();
        await this.props.structureViewerInstance.visual.select({ data: this.props.visibleMutations });

    }

    getSliderMarksDdg() {
        let marks = { [this.state.currentMax]: { style: { color: "black" }, label: this.state.currentMax }, [this.state.currentMin]: { style: { color: "black" }, label: this.state.currentMin } };
        if (this.addOneDdg) {
            marks[1] = { style: { color: "black" }, label: "1" };
        }
        if (this.addMinusOneDdg) {
            marks["-1"] = { style: { color: "black" }, label: "-1" };
        }


        return marks;
    }
    getSliderMarksDtm() {
        let marks = { [this.state.currentMax]: { style: { color: "black" }, label: this.state.currentMax }, [this.state.currentMin]: { style: { color: "black" }, label: this.state.currentMin } };
        if (this.addMinusOneDtm) {
            marks["-1"] = { style: { color: "black" }, label: "-1" };
        }
        if (this.addOneDtm) {
            marks[1] = { style: { color: "black" }, label: "1" };
        }

        return marks;
    }

    tooltipFormat = (value) => {
        if (this.props.option === "ddG") {
            if (value < -1) {
                return <span style={{ fontSize: "14px", color: "#a5d6a7" }}>{"Stabilizing"}</span>
            }
            else if (value >= -1 && value <= 1) {
                return <span style={{ fontSize: "14px", color: "#81d4fa" }}>{"Neutral"}</span>
            }
            else {
                return <span style={{ fontSize: "14px", color: "#e57373" }}>{"Destabilizing"}</span>
            }
        }
        else {
            if (value < -1) {
                return <span style={{ fontSize: "14px", color: "#e57373" }}>{"Destabilizing"}</span>
            }
            else if (value >= -1 && value <= 1) {
                return <span style={{ fontSize: "14px", color: "#81d4fa" }}>{"Neutral"}</span>
            }
            else {
                return <span style={{ fontSize: "14px", color: "#a5d6a7" }}>{"Stabilizing"}</span>
            }
        }
    }
    render() {
   
        return <div className="slider" style={{opacity: (this.state.disableSlider) ? "0.6" : "1.0", pointerEvents: (this.state.disableSlider) ? "none" : "all"}}>
            <Range
                min={this.props.option === "ddG" ? this.ddgSliderMinValue : this.dtmSliderMinValue}
                max={this.props.option === "ddG" ? this.ddgSliderMaxValue : this.dtmSliderMaxValue}
                step={0.01}
                defaultValue={[0, 0]}
                marks={this.props.option === "ddG" ? this.getSliderMarksDdg() : this.getSliderMarksDtm()}

                onAfterChange={async (value) => {
                    this.setState({ currentSliderRange: value })
                    this.props.setSliderValue({
                        sliderMax: value[1],
                        sliderMin: value[0],
                        option: this.props.option === "ddG" ? "mutexperiment.ddg" : "mutexperiment.dtm"
                    })
                    this.props.option === "ddG" ? await this.setCurrentDdgPositions(value) : await this.setCurrentDtmPositions(value)
                }}
                allowCross={false}
                activeDotStyle={{ color: "orange" }}
                handleStyle={[{ backgroundColor: "#81d4fa" }]}
                railStyle={{ backgroundColor: "#C8C8C8" }}
                dotStyle={{ border: "1px solid black" }}
                tipFormatter={value => this.tooltipFormat(value)}
            />

        </div>
    }



}

export default StabilitySliderComponent;