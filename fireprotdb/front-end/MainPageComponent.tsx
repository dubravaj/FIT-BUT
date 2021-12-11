import React, { Component } from 'react';
import Header from './layout/Header';
import HelpPageComponent from "./HelpPageComponent";
import { NavBarContainer } from "../containers/SearchContainer";
import { MutationPageContainer } from "../containers/MutationContainer";
import { ProteinSequencePageContainer } from "../containers/protein/ProteinSequenceContainer";
import { SearchResultsContainer } from "../containers/SearchResultsContainer";
import { DatasetPageContainer } from "../containers/DatasetContainer";
import { SideBarContainer } from "../containers/SideBarContainer";
import { FullTextSearchContainer } from "../containers/SearchContainer";
import { DatasetsListPageContainer } from "../containers/DatasetsListContainer";
import Main from "./layout/Main";
import Content from './layout/Content';
import MainComponent from "./layout/MainComponent";
import ErrorPageComponent from "./ErrorPageComponent";
import AcknowledgementPage from "./AcknowledgementPage";
import UseCasesPageComponent from "./UseCasesPageComponent";
import { Router,Route, Switch } from "react-router-dom";
import { createBrowserHistory } from 'history';
import SwaggerUI from "swagger-ui-react";

const history = createBrowserHistory({
  basename: process.env.PUBLIC_URL
}) 
;

const Swagger = () => {
  return <div className="fpdb-container swagger-container"> 
        <SwaggerUI syntaxHighlight="false" url={"./v1/api-docs"} />
        </div>
}


class MainPageComponent extends Component {

  render() {

    return (
      <Router history={history}>
       
        <div className="fireprotdb-main-page" id={"main-page"}>
          <Header />
          <FullTextSearchContainer />
          <NavBarContainer />
          <Main>
            <Content>
              <Switch>
                <Route exact path={"/"} key="search" component={MainComponent} />
                <Route path={"/results"} key="results" component={SearchResultsContainer} />
                <Route path={"/protein/:sequenceId"} key="protein" component={ProteinSequencePageContainer} />
                <Route path={"/dataset/:datasetId"} key="dataset" component={DatasetPageContainer} />
                <Route path={"/help"} key="help" component={HelpPageComponent} />
                <Route path={"/acknowledgement"} key="acknowledgement" component={AcknowledgementPage} />
                <Route path={"/use-cases"} key="use-cases" component={UseCasesPageComponent} />
                <Route path={"/mutation/:mutationId"} key="mutation" component={MutationPageContainer} />
                <Route path={"/datasets"} key="datasets" component={DatasetsListPageContainer}/>
                <Route component={ErrorPageComponent} />
              </Switch>
            </Content>
            <SideBarContainer />
          </Main>
        </div>
      </Router>
    )
  }
}

export default MainPageComponent;
