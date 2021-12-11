import React, { Component } from "react";
import { Link } from "react-router-dom";
import { withStyles } from "@material-ui/core/styles";
import styles from '../../styles/searchStyle';
import { SearchResultsPropsData, SearchResultsPropsFunctions } from "../../interface/props/SearchResultsProps";

type ResultMutationProps = {
  mutation: any;
}

class SearchResultMutationComponent extends Component<SearchResultsPropsData & SearchResultsPropsFunctions & ResultMutationProps>{

  render() {

    return (
      <div className="mutation-item">
        <div className="mutation-id">
          <div className="label">Entry</div>
          <div className="text">
            <Link className="text" to={`/mutation/${this.props.mutation.id}`}>
              {this.props.mutation.id}
            </Link>
          </div>
        </div>
        <div className="mutation-info">
          <div className="experiments">
            <div className="label"> Number of experiments: </div>
            <div className="text">{this.props.mutation.mutationExperiments.length}</div>
          </div>
        </div>
      </div>
    )
  }
}

export default withStyles(styles)(SearchResultMutationComponent);
