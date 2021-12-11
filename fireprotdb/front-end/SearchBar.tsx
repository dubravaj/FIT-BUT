import React, { Component } from "react";
import { SearchComponentPropsData, SearchComponentPropsFunctions } from "../../interface/props/SearchProps";
import { SearchItemContainer } from "../../containers/SearchItemContainer";
import Button from '@material-ui/core/Button';
import styles from '../../styles/searchStyle';
import { withStyles } from "@material-ui/core/styles";
import SearchIcon from "@material-ui/icons/Search";
import CloseIcon from '@material-ui/icons/Close';
import CircularProgress from '@material-ui/core/CircularProgress';
import {pageSize} from '../../settings';

class SearchBar extends Component<SearchComponentPropsData & SearchComponentPropsFunctions> {

  componentDidMount() {
    this.props.getSearchItemsOptions();
  }

  searchingIcon = (isSearching:boolean) => {
    if (isSearching) {
      return (
        <div>
          <CircularProgress className={this.props.classes.loadingIcon} size={20} />
              Searching...
        </div>
      )
    }
    else {
      return (
        <div>
          <SearchIcon />
              Search
        </div>
      )
    }
  };


  render() {
    const classes = this.props.classes

    return (
      <div className="search-bar">
        <div className="search-bar-content">
          <div className="search-label">
            <div className="text">Advanced search</div>
            <div >
              <Button style={{ color: "white", float: "right", marginTop: "-10px" }}
                onClick={() => {
                  this.props.setShowAdvancedSearch(false)
                }
                }><CloseIcon /></Button>
            </div>
          </div>
          <div className="search-label-line"></div>
          <div className="search-items">
            {this.props.searchItems.map((val, index) => <SearchItemContainer history={this.props.history} key={index} id={index} item={val} shown={this.props.addButtonIsShown[index]} disableInput={false} />)}
          </div>
          <div className="search-button">
            <Button disabled={this.props.isSearching}
              onClick={(evt) => {
                this.props.setAllSearchItem();
                this.props.setShowAllData(true);
                this.props.setShowResultsStatistics(false);
                this.props.fetchAllRequestData({ filterKey: "ddG", order: "asc" }, this.props.history, 0, pageSize);
              }} variant="contained" className={classes.searchButton}
              style={{ backgroundColor: this.props.isSearching ? "#9e9e9e" : "#e78a20" }}
            >
              Show all data
           </Button>
            <Button type="submit"
              disabled={this.props.isSearching}
              onClick={() => {
                this.props.setShowAllData(false);
                this.props.setShowResultsStatistics(false);
                this.props.fetchRequestData(this.props.searchItems, { filterKey: "ddG", order: 'asc' }, this.props.history, 0, pageSize);
                this.props.setSearchItemsFetching(this.props.searchItems);
              }} variant="contained" className={classes.searchButton} style={{ backgroundColor: this.props.isSearching ? "#9e9e9e" : "#e78a20" }}>
              {
                this.searchingIcon(this.props.isSearching)
              }
            </Button >
            <Button variant="contained" className={classes.searchButton} onClick={() => this.props.resetSearch()}
              disabled={this.props.isSearching} style={{ backgroundColor: this.props.isSearching ? "#9e9e9e" : "#e78a20" }}>
              {"Reset"}
            </Button>
          </div>
        </div>
      </div>
    )
  }
};
export default withStyles(styles)(SearchBar);
