import React, { Component } from "react";
import Button from '@material-ui/core/Button';
import styles from '../../styles/searchStyle';
import { withStyles } from "@material-ui/core/styles";
import SearchIcon from "@material-ui/icons/Search";
import CircularProgress from '@material-ui/core/CircularProgress';
import { Input } from 'reactstrap';
import { SearchComponentPropsData, SearchComponentPropsFunctions } from "../../interface/props/SearchProps";
import { SearchContainer } from "../../containers/SearchContainer";
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';

class FullTextSearch extends Component<SearchComponentPropsData & SearchComponentPropsFunctions>{

  loadIcon = (isSearching:boolean) => {
    if (isSearching) {
      return (
        <div>
          <CircularProgress className={this.props.classes.loadingIcon} size={20} />
        </div>
      );
    }
    else {
      return (
        <div>
          <SearchIcon />
        </div>
      );
    }
  };

  advancedSearch = (showAdvanced:boolean) => {
    let advancedClass;
    if (showAdvanced) {
      advancedClass = "advancedSearchShown";
    }
    else {
      advancedClass = "advancedSearchHidden";
    }
    return (
      <div className={advancedClass}>
        <SearchContainer />
      </div>
    );
  }

  render() {
    const classes = this.props.classes;
    return (
      <div className="full-text-search">

        <div className="items">
          <div className="search-label">
            <label className="label">Search</label>
            <div className="search-field">
              <Input type="text" name="search" id="searchField" placeholder="Enter search phrase..." className="input"
                value={this.props.fullTextSearchValue}
                onChange={(event) => this.props.setSearchFulltextValue(event.target.value)}
              />
              <div className="buttons">
                <div className="advanced-btn">
                  <Button variant="text" endIcon={<ArrowDropDownIcon />} className={classes.advancedSearchBtn}
                    onClick={() => {
                      this.props.setShowAdvancedSearch(true)
                    }}
                  >Advanced</Button>
                </div>
                <div className="search-button">
                  <Button type="submit" variant="text" className={classes.fulltextSearchButton}
                    onClick={() => {
                      if (this.props.fullTextSearchValue === "") {
                        this.props.setAllSearchItem();
                        this.props.setShowAllData(true);
                        this.props.setShowResultsStatistics(false);
                        this.props.fetchAllRequestData({ filterKey: "ddG", order: "asc" }, this.props.history, 0, 20);
                        this.props.setShowAdvancedSearch(false);
                      }
                      else {
                        this.props.setShowResultsStatistics(false);
                        this.props.setShowAllData(false);
                        this.props.setFulltextValueFetching(this.props.fullTextSearchValue);
                        this.props.fetchFullTextData(this.props.fullTextSearchValue, { filterKey: "ddG", order: "asc" }, this.props.history, 0, 20)
                      }
                    }}
                    disabled={this.props.isSearchingFulltext}
                  >
                    {
                      this.loadIcon(this.props.isSearchingFulltext)
                    }
                  </Button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div className="advanced-container">
          {
            this.advancedSearch(this.props.showAdvancedSearch)
          }
        </div>
      </div>
    );
  }
}

export default withStyles(styles)(FullTextSearch);
