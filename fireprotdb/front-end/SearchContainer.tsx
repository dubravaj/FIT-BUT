import { connect } from "react-redux";
import { withRouter } from "react-router-dom";
import { Dispatch } from "redux";
import AppState from "../interface/AppState";
import { SearchComponentPropsData, SearchComponentPropsFunctions } from "../interface/props/SearchProps";
import { SearchRequestOperatorObject } from "../interface/props/SearchRequestOperatorObject";
import { SearchRequestExpressionObject } from "../interface/props/SearchRequestExpressionObject";
import SearchRequestObject from "../interface/props/SearchRequestObject";
import SearchBar from "../components/layout/SearchBar";
import SearchActions from "../actions/search-actions";
import NavBar from "../components/layout/NavBar";
import FullTextSearch from "../components/layout/FullTextSearch";
import SearchItemState, { SearchOptions } from "../interface/SearchItemState";
import { SliderValue } from "../interface/ProteinState";
import { status } from "../settings";

let codec = require('json-url')('lzw')
type CheckOptions = {
  value: string;
}

const AAs = ['A', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'Y']

function hasNumbers(searchValue) {
  var regex = /^\d*$/;
  return regex.test(searchValue);
}


function isAlphaNumeric(searchValue) {
  if (searchValue.match("^[a-zA-Z0-9]*$")) {
    return true;
  }
  return false;

}

function isFloat(searchValue) {
  var regex = /^(-)?[0-9]+(\.)?[0-9]*$/;
  return regex.test(searchValue);
}

/**
 * Check options values in search items
 * @param searchItems search items
 */
function checkSameCheckboxOptions(searchItems: SearchItemState[]) {
  var options = new Map<string, any[]>();
  var badOptions = false;
  for (var item of searchItems) {
    if (options[item.searchOption.value]) {
      options[item.searchOption.value].push(item);
    }
    else {
      options[item.searchOption.value] = [];
      options[item.searchOption.value].push(item);
    }
  }

  for (let [, value] of Object.entries(options)) {
    let currArr = value;
    let requiredOption;
    if (currArr.length === 0) {
      requiredOption = "";
    }
    else {
      let optionString = "";
      currArr[0].checkOptions.forEach(value => {
        optionString = optionString.concat(value.value);
      });
      requiredOption = optionString;
    }

    for (var o of currArr) {
      let currOptionString = "";
      if (o.checkOptions.length === 0) {
        currOptionString = "";
      }
      else {
        o.checkOptions.forEach(value => {
          currOptionString = currOptionString.concat(value.value);
        });
      }
      if (currOptionString !== requiredOption) {
        alert("Checked options have to be same for all same search options");
        return true;
      }
    }
  }

  return badOptions;
}


/**
 * Check options values in search items
 * @param searchItems search items
 */
function checkSearchOptionValue(searchItems: SearchItemState[]) {

  for (let i = 0; i < searchItems.length; i++) {

    if (searchItems[i].searchOption.value === "all" && searchItems.length > 1) {
      alert("Cannot combine all option with another.");
      return true;
    }

    if (searchItems[i].logicOperator.value === "" && i !== 0) {
      alert("No operator selected");
      return true;
    }
    if (searchItems[i].searchOption.value === "") {
      alert("No option selected");
      return true;
    }
    else {

      switch (searchItems[i].searchOption.value) {

        case "sequence.sequence":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          break;
        case "sequence.uniprotId":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          else if (!isAlphaNumeric(searchItems[i].searchedValue)) {
            alert("Please enter alphanumeric value");
            return true;
          }
          break;

        case "sequence.genus":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          else if (hasNumbers(searchItems[i].searchedValue)) {
            alert("Please enter value containing only letters.");
            return true;
          }
          break;
        case "sequence.ecnumber":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          break;
        case "sequence.family":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          };
          break;
        case "residue.position":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          else if (!hasNumbers(searchItems[i].searchedValue)) {
            alert("Please enter number.");
            return true;
          }
          break;
        case "residue.secstructure":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          break;
        case "mutation.aminoacid":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          else if (searchItems[i].searchedValue.length !== 1) {
            alert("Plese enter single letter.");
            return true;
          }
          else if (!AAs.includes(searchItems[i].searchedValue.toUpperCase())) {
            alert("Plese enter valid aminoacid.");
            return true;
          }
          searchItems[i].searchedValue = searchItems[i].searchedValue.toUpperCase();
          break;
        case "mutexperiment.ddg":
        case "mutexperiment.dtm":
        case "mutexperiment.cp":
        case "mutexperiment.toffset":
        case "mutexperiment.ph":
        case "mutexperiment.tm":
        case "mutexperiment.proteinconcentration":
        case "mutexperiment.samplepurity":
        case "mutexperiment.halflife":
        case "mutexperiment.scanrate":

          if (searchItems[i].compareOperator.value === "") {
            alert("No operator selected.");
            return true;
          }
          if (searchItems[i].searchedValue === "") {
            alert("No input value entered.")
            return true;
          }
          else {
            if (!isFloat(searchItems[i].searchedValue)) {
              alert("Please enter float value.");
              return true;
            }
          }
          break;
        case "dataset.name":
        case "sequence.species":
        case "sequence.hasinterprofamily":
          if (searchItems[i].compareOperator.value === "") {
            alert("No operator selected.");
            return true;
          }
          if (searchItems[i].multiOption.length === 0) {
            alert("No input value entered.")
            return true;
          }
          break;
        case "publication.doi":
          if (searchItems[i].searchedValue === "") {
            alert("No value entered.");
            return true;
          }
          break;
      }
    }
  }
}


/**
 * Create expression object from search item
 * @param elem1 search item element
 */
function createExpression(elem1: SearchItemState) {

  let searchedValueElem1 = "";
  let multiOptionValuesElem1: string[] = [];
  let multiOptionValElem1 = "";
  let checkOptionsElem1: CheckOptions[] = [];

  // check if there is multioption in search item
  if (elem1.multiOption.length > 0) {
    for (let option of elem1.multiOption) {
      multiOptionValuesElem1.push(option.value);
    }
  }

  // check if there is checkoption in search item
  if (elem1.checkOptions.length > 0) {
    for (let option of elem1.checkOptions) {
      checkOptionsElem1.push({ value: option.value });
    }
  }

  // search item contains comparing operator
  if (elem1.compareOperator.value !== "") {
    if (elem1.multiOption.length > 0) {
      // concatenate values from multioption
      multiOptionValElem1 = multiOptionValuesElem1.join(";");
      searchedValueElem1 = elem1.compareOperator.value.concat(" ").concat(multiOptionValElem1);
    }
    else {
      searchedValueElem1 = elem1.compareOperator.value.concat(" ").concat(elem1.searchedValue);
    }
  }
  // normal search value
  else {
    searchedValueElem1 = elem1.searchedValue;
  }

  // create new expression object with search value and check options
  let expression = new SearchRequestExpressionObject("expr", elem1.searchOption.value, searchedValueElem1, checkOptionsElem1);

  return expression;
}


/**
 * Check number of brackets in search query
 * @param tokens array of tokens
 */
function checkNumberOfBrackets(tokens: any[]) {
  let leftBrackets: string[] = [];
  let rightBrackets: string[] = [];
  for (const token of tokens) {
    if (token === '(') {
      leftBrackets.push(token);
    }
    else if (token === ')') {
      rightBrackets.push(token);
    }
  }
  if (leftBrackets.length !== rightBrackets.length) {
    return false;
  }
  return true;
}

/**
 * Create postfix notation array of search items 
 * @param searchItems array of search items
 */
function createPostFix(searchItems: SearchItemState[]) {

  let tokens: any[] = [];
  let precedence: string[] = ['or', 'and'];
  let operatorsStack: any[] = [];
  let postFixItems: any[] = [];

  // create tokens array from search items
  for (const item of searchItems) {
    // push logic operator to tokens
    if (item.logicOperator.value !== '') {
      tokens.push(item.logicOperator.value);
    }
    //push left bracket to tokens
    if (typeof item.leftBracket === 'object') {
      item.leftBracket.value.forEach(bracket => {
        tokens.push(bracket);
      });
    }
    // push search item object to tokens
    tokens.push(createExpression(item));

    // push right bracket to tokens
    if (typeof item.rightBracket === 'object') {
      item.rightBracket.value.forEach(bracket => {
        tokens.push(bracket);
      });
    }
  }

  // check number of brackets
  if (!checkNumberOfBrackets(tokens)) {
    return [];
  }

  // parse token and create postfix notation
  for (const token of tokens) {
    // token is search item object
    if (typeof token === 'object') {
      postFixItems.push(token);
      continue;
    }

    let stackTop = operatorsStack.length > 0 ? operatorsStack[operatorsStack.length - 1] : '';

    // stack is empty or top of the stack contains left bracket
    if (stackTop === '' || stackTop === '(') {
      operatorsStack.push(token);
      continue;
    }
    // current token is left bracket
    if (token === '(') {
      operatorsStack.push(token);
      continue;
    }
    // current token is right bracket
    if (token === ')') {
      let currItem = operatorsStack.pop();
      while (currItem !== '(') {
        postFixItems.push(currItem);
        currItem = operatorsStack.pop();
      }
      continue;
    }
    // token is operator
    let currItemPrecedence: number = precedence.indexOf(token);
    let stackTopPrecedence: number = precedence.indexOf(stackTop);
    while (currItemPrecedence < stackTopPrecedence) {
      let op = operatorsStack.pop();
      postFixItems.push(op);
      stackTopPrecedence = precedence.indexOf(operatorsStack[operatorsStack.length - 1]);
    }
    operatorsStack.push(token);

  }

  // push remaining operators to postfix array
  while (operatorsStack.length > 0) {
    let op: any = operatorsStack.pop();
    if (op !== '(') {
      postFixItems.push(op);
    }
  }

  return postFixItems;
}

/**
 * Create search request object from array of search items
 * @param searchItems search items array
 * @param filter search filter for sorting
 */
export function createPredicatesRequest(searchItems: SearchItemState[], filter) {
  // check options
  var checkResult = checkSearchOptionValue(searchItems);
  if (checkResult) {
    return null;
  }

  // check correctness of search values
  let checkboxesResult = checkSameCheckboxOptions(searchItems);
  if (checkboxesResult) {
    return null;
  }

  let tokens: any[] = [];
  let stack: any[] = [];

  // create tokens array from search items
  tokens = createPostFix(searchItems);
  if (tokens.length === 0) {
    alert('Missing corresponding bracket in search query.');
    return null;
  }

  let operations = {
    'and': (expr1, expr2) => {
      let predicate = new SearchRequestOperatorObject();
      predicate.setType('and');
      predicate.addOption(expr1);
      predicate.addOption(expr2);
      return predicate;
    },
    'or': (expr1, expr2) => {
      let predicate = new SearchRequestOperatorObject();
      predicate.setType('or');
      predicate.addOption(expr1);
      predicate.addOption(expr2);
      return predicate;
    }
  }

  // parse postfix array and generate predicates
  tokens.forEach((token) => {

    switch (token) {
      case 'and':
      case 'or':
        let exp1 = stack.pop();
        let exp2 = stack.pop();
        stack.push(operations[token](exp1, exp2));
        break;
      default:
        stack.push(token);
    };
  })

  let requestBody: any = stack.pop();
  let searchRequest: SearchRequestObject = new SearchRequestObject(requestBody, filter);

  return searchRequest;

}


/**
 * Get data from search
 * @param dispatch Dispatch
 * @param requestBody search request
 * @param pageNumber page number 
 * @param pageSize page size
 */
async function getSearchRequestResponse(dispatch: Dispatch, searchType: string, requestBody: SearchRequestObject, pageNumber: number, pageSize: number): Promise<any> {
  try {
    const response = await fetch(`./v1/search?type=${searchType}&page=${pageNumber}&size=${pageSize}`, {
      method: 'post',
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(requestBody)
    });
    await status(response);
    const data = await response.json();
    return data;

  }
  catch (error) {
    dispatch(SearchActions.setIsSearching(false));
    return Promise.reject();
  }
}

/**
 * Set search results data
 * @param dispatch dispatch
 * @param searchPage page number
 * @param history hsistory
 * @returns Promise
 */
async function setSearchResults(dispatch: Dispatch, searchPage, history): Promise<any> {
  return new Promise((resolve, reject) => {
    dispatch(SearchActions.setTotatItemsCount(searchPage.searchResultsCount));
    dispatch(SearchActions.setTotalPageCount(searchPage.totalPageCount));
    dispatch(SearchActions.addSearchResult(searchPage.searchResults));
    resolve({});
  });
}
/**
 * Create search request object for all data
 */
export function createAllDataRequest() {
  return new SearchRequestObject(new SearchRequestExpressionObject("expr", "all", "", []), { filterKey: "ddG", order: "asc" });
}

/**
 * Parse input data from chart label to be used as search values 
 * @param chartData chart label data
 * @returns object with type of interval and values of interval
 */
function parseDdgDtmChartInput(chartData: string) {
  let value:number;
  let value2:number;
  let isInterval:boolean = false;
  let isLessEq:boolean = false;
  let isGreater:boolean = false;

  // parse input value 
  if (chartData.includes("(")) {
    isInterval = true;
    [value, value2] = chartData.slice(1, chartData.length - 1).split(",");
  }
  else if (chartData.includes("≤")) {
    isLessEq = true;
    value = chartData.split(" ")[1];
  }
  else if (chartData.includes(">")) {
    isGreater = true;
    value = chartData.split(" ")[1];
  }

  return {
    "isGreater": isGreater,
    "isInterval": isInterval,
    "isLessEq": isLessEq,
    "value": value,
    "value2": value2
  }
}

/**
 * Create search object data from currently clicked bar in chart
 * @param option type of chart
 * @param chartData chart label data
 * @returns search object
 */
function createDtmDdgSearchData(option: string, chartData: string) {

  let searchData: SearchRequestExpressionObject | SearchRequestOperatorObject;
  let value:number;
  let value2:number;
  let result: any = parseDdgDtmChartInput(chartData);
  let searchOption: string = (option === "ddG") ? "mutexperiment.ddg" : "mutexperiment.dtm";

  if (result.isInterval) {
    value = result.value;
    value2 = result.value2;
    let expr1 = new SearchRequestExpressionObject("expr", searchOption, `> ${value}`, []);
    let expr2 = new SearchRequestExpressionObject("expr", searchOption, `<= ${value2}`, []);
    let req = new SearchRequestOperatorObject();
    req.setType("and");
    req.addOption(expr1);
    req.addOption(expr2);
    searchData = req;
  }
  else if (result.isLessEq) {
    value = result.value;
    searchData = new SearchRequestExpressionObject("expr", searchOption, `<= ${value}`, []);
  }
  else if (result.isGreater) {
    ;
    value = result.value;
    searchData = new SearchRequestExpressionObject("expr", searchOption, `> ${value}`, []);
  }
  return searchData;

}
/**
 * Create search data from slider range and currently viewed protein
 * @param sliderValue slider value with range of values and type of value
 * @param proteinName protein name
 * @returns search request operator object representing search request
 */
function createSliderSearchData(sliderValue: SliderValue, proteinName: string) {

  if (sliderValue.option !== "mutexperiment.ddg" && sliderValue.option !== "mutexperiment.dtm") {
    return null;
  }

  let sliderRequest;
  let proteinExpr = new SearchRequestExpressionObject("expr", "sequence.name", proteinName, []);
  let expr1: SearchRequestExpressionObject;
  let expr2: SearchRequestExpressionObject;
  let tmpExpr: SearchRequestOperatorObject;

  expr1 = new SearchRequestExpressionObject("expr", sliderValue.option, `>= ${sliderValue.sliderMin}`, []);
  expr2 = new SearchRequestExpressionObject("expr", sliderValue.option, `<= ${sliderValue.sliderMax}`, []);
  tmpExpr = new SearchRequestOperatorObject();
  tmpExpr.setType("and");
  tmpExpr.addOption(expr1);
  tmpExpr.addOption(expr2);
  sliderRequest = new SearchRequestOperatorObject();
  sliderRequest.setType("and");
  sliderRequest.addOption(tmpExpr);
  sliderRequest.addOption(proteinExpr);

  return sliderRequest;
}


/**
 * Create query for URL state
 * @param dispatch dispatch 
 * @param searchOption search option according to chart type
 * @param value value of selected range in chart
 * @returns search state object
 */
function createUrlQueryRequest(dispatch: Dispatch, searchOption: string, value: any) {
  let searchItems: any[] = [];
  let result: any = parseDdgDtmChartInput(value);

  if (searchOption === "sequence.name") {
    searchItems = [{
      showCheckOptionsBar: false,
      leftBracket: {
        value: [],
        label: ''
      },
      rightBracket: {
        value: [],
        label: ''
      },
      logicOperator: {
        value: "",
        label: ""
      },
      searchOption: {
        value: searchOption,
        label: SearchOptions.find((value,) => value.value === searchOption)?.label
      },
      searchedValue: value,
      compareOperator: {
        value: "",
        label: ""
      },
      booleanOperator: "",
      multiOption: [],
      checkOptions: []
    }];
  }
  else if (searchOption === 'sequence.hasinterprofamily') {
    searchItems = [{
      showCheckOptionsBar: false,
      leftBracket: {
        value: [],
        label: ''
      },
      rightBracket: {
        value: [],
        label: ''
      },
      logicOperator: {
        value: "",
        label: ""
      },
      searchOption: {
        value: searchOption,
        label: SearchOptions.find((value,) => value.value === searchOption)?.label
      },
      searchedValue: "",
      compareOperator: {
        value: "in",
        label: "in"
      },
      booleanOperator: "",
      multiOption: [{ value: value, label: value }],
      checkOptions: []
    }];
  }
  else if (searchOption === "mutexperiment.ddg" || searchOption === "mutexperiment.dtm") {
    searchItems = [{
      showCheckOptionsBar: false,
      leftBracket: {
        value: [],
        label: ''
      },
      rightBracket: {
        value: [],
        label: ''
      },
      logicOperator: {
        value: "",
        label: ""
      },
      searchOption: {
        value: searchOption,
        label: SearchOptions.find((value,) => value.value === searchOption)?.label
      },
      searchedValue: result.value,
      compareOperator: {
        value: ">",
        label: ">"
      },
      booleanOperator: "",
      multiOption: [],
      checkOptions: []
    },
    {
      showCheckOptionsBar: false,
      leftBracket: {
        value: [],
        label: ''
      },
      rightBracket: {
        value: [],
        label: ''
      },
      logicOperator: {
        value: "and",
        label: "AND"
      },
      searchOption: {
        value: searchOption,
        label: SearchOptions.find((value,) => value.value === searchOption)?.label
      },
      searchedValue: result.value2,
      compareOperator: {
        value: "<=",
        label: "<="
      },
      booleanOperator: "",
      multiOption: [],
      checkOptions: []
    }];
  }
  dispatch(SearchActions.resetSearchItems());
  searchItems.forEach((item, idx) => {
    dispatch(SearchActions.addSearchItemUrl({ item: item, showAddBtn: (idx !== searchItems.length - 1) ? false : true }));
  })
  dispatch(SearchActions.setSearchItemsFetching(searchItems));
  return searchItems;
}

/**
 * Create url search object for slider search
 * @param dispatch dispatch
 * @param proteinName protein name
 * @param sliderValue slider value with minimum, maximum values, option 
 * @returns search items used to search
 */
function createUrlSliderQuery(dispatch: Dispatch, proteinName: string, sliderValue: SliderValue) {
  let searchItems: any[] = [];
  searchItems = [{
    showCheckOptionsBar: false,
    leftBracket: {
      value: [],
      label: ''
    },
    rightBracket: {
      value: [],
      label: ''
    },
    logicOperator: {
      value: "",
      label: ""
    },
    searchOption: {
      value: "sequence.name",
      label: SearchOptions.find((value,) => value.value === "sequence.name")?.label
    },
    searchedValue: proteinName,
    compareOperator: {
      value: "",
      label: ""
    },
    booleanOperator: "",
    multiOption: [],
    checkOptions: []
  },
  {
    showCheckOptionsBar: false,
    leftBracket: {
      value: [],
      label: ''
    },
    rightBracket: {
      value: [],
      label: ''
    },
    logicOperator: {
      value: "and",
      label: "AND"
    },
    searchOption: {
      value: sliderValue.option,
      label: SearchOptions.find((value,) => value.value === sliderValue.option)?.label
    },
    searchedValue: sliderValue.sliderMin,
    compareOperator: {
      value: ">=",
      label: ">="
    },
    booleanOperator: "",
    multiOption: [],
    checkOptions: []
  },
  {
    showCheckOptionsBar: false,
    leftBracket: {
      value: [],
      label: ''
    },
    rightBracket: {
      value: [],
      label: ''
    },
    logicOperator: {
      value: "and",
      label: "AND"
    },
    searchOption: {
      value: sliderValue.option,
      label: SearchOptions.find((value,) => value.value === sliderValue.option)?.label
    },
    searchedValue: sliderValue.sliderMax,
    compareOperator: {
      value: "<=",
      label: "<="
    },
    booleanOperator: "",
    multiOption: [],
    checkOptions: []
  }];

  dispatch(SearchActions.resetSearchItems());
  searchItems.forEach((item, idx) => {
    dispatch(SearchActions.addSearchItemUrl({ item: item, showAddBtn: (idx !== searchItems.length - 1) ? false : true }));
  })
  dispatch(SearchActions.setSearchItemsFetching(searchItems));

  return searchItems;

}


/**
 * Create search item for fetching all data from URL
 */
function createUrlQueryAllRequest(dispatch: Dispatch) {
  let searchItems: any[] = [{
    showCheckOptionsBar: false,
    leftBracket: {
      value: [],
      label: ''
    },
    rightBracket: {
      value: [],
      label: ''
    },
    logicOperator: {
      value: "",
      label: ""
    },
    searchOption: {
      value: "all",
      label: " ",
    },
    searchedValue: " ",
    compareOperator: {
      value: "",
      label: ""
    },
    booleanOperator: "",
    multiOption: [],
    checkOptions: []
  }];
  dispatch(SearchActions.setSearchItemsFetching(searchItems));
  return searchItems;
}


/**
 * Get all data
 * @param dispatch dispatch
 * @param filter filter for results
 * @param history history
 * @param pageNumber page number
 * @param pageSize page size
 */
export async function fetchAllRequestData(dispatch: Dispatch, filter, history, pageNumber: number, pageSize: number): Promise<any> {

  dispatch(SearchActions.setPageNumber(0));
  dispatch(SearchActions.setSearchResultsAdvancedSearch(true));
  dispatch(SearchActions.setSearchResultsFullTextSearch(false));
  dispatch(SearchActions.setSearchResultsShowStatistics(false));
  dispatch(SearchActions.setShowAdvancedSearch(true));
  let searchData = new SearchRequestExpressionObject("expr", "all", "", []);
  let requestBody = new SearchRequestObject(searchData, filter);

  if (requestBody === null) {
    return;
  }
  dispatch(SearchActions.setIsSearching(true));
  let searchType: string = "advanced";

  const searchResults = await getSearchRequestResponse(dispatch, searchType, requestBody, pageNumber, pageSize);
  await setSearchResults(dispatch, searchResults, history);

  dispatch(SearchActions.setShowResults(true));
  dispatch(SearchActions.setIsSearching(false));
  dispatch(SearchActions.setShowAdvancedSearch(false));

  let searchItems = createUrlQueryAllRequest(dispatch);

  const data = await codec.compress(searchItems);
  history.push({ pathname: "/results", search: `type=${searchType}`.concat(`&`).concat(data) });
}

/**
 * Fetch data from clicking in main page charts
 * @param dispatch dispatch
 * @param chartData chart data 
 * @param dataType type of currently used chart
 * @param filter filter object
 * @param history history
 * @param pageNumber page number
 * @param pageSize page size
 * @returns void
 */
export async function fetchChartRequestData(dispatch: Dispatch, chartData: any, dataType: string, filter, history, pageNumber: number, pageSize: number): Promise<any> {

  dispatch(SearchActions.setPageNumber(0));
  dispatch(SearchActions.setSearchResultsAdvancedSearch(true));
  dispatch(SearchActions.setSearchResultsFullTextSearch(false));
  dispatch(SearchActions.setSearchResultsShowStatistics(false));
  dispatch(SearchActions.setShowAdvancedSearch(false));
  dispatch(SearchActions.setShowAllData(false));
  let searchData;

  // create search data according to selected chart
  switch (dataType) {
    case "protein":
      searchData = new SearchRequestExpressionObject("expr", "sequence.name", chartData, []);
      break;
    case "family":
      searchData = new SearchRequestExpressionObject("expr", "sequence.hasinterprofamily", "in".concat(" ").concat(chartData), []);
      break;
    case "ddG":
    case "dTm":
      searchData = createDtmDdgSearchData(dataType, chartData);
      break;
  }

  let requestBody = new SearchRequestObject(searchData, filter);

  if (requestBody === null) {
    return;
  }
  dispatch(SearchActions.setIsSearching(true));
  let searchType = "advanced";

  const searchResults = await getSearchRequestResponse(dispatch, searchType, requestBody, pageNumber, pageSize);
  await setSearchResults(dispatch, searchResults, history);

  dispatch(SearchActions.setShowResults(true));
  dispatch(SearchActions.setIsSearching(false));


  let option;

  // create search state according data chosen from chart
  switch (dataType) {
    case "protein":
      option = "sequence.name";
      break;
    case "family":
      option = "sequence.hasinterprofamily";
      break;
    case "ddG":
      option = "mutexperiment.ddg";
      break;
    case "dTm":
      option = "mutexperiment.dtm";
      break;
  }

  let searchItems = createUrlQueryRequest(dispatch, option, chartData);
  const data = await codec.compress(searchItems);
  history.push({ pathname: "/results", search: `type=${searchType}`.concat(`&`).concat(data) });
}

/**
 * Fetch data based on protein and currently selected range in slider
 * @param dispatch dispatch
 * @param sliderValue value of slider with range minimum, maximum, option value
 * @param proteinName currently viewed protein name
 * @param filter filter object
 * @param history history
 * @param pageNumber page number
 * @param pageSize page size
 */
export async function fetchSliderRequestData(dispatch: Dispatch, sliderValue: SliderValue, proteinName: string, filter, history, pageNumber: number, pageSize: number) {

  dispatch(SearchActions.setPageNumber(0));
  dispatch(SearchActions.setSearchResultsAdvancedSearch(true));
  dispatch(SearchActions.setSearchResultsFullTextSearch(false));
  dispatch(SearchActions.setSearchResultsShowStatistics(false));
  dispatch(SearchActions.setShowAdvancedSearch(false));
  dispatch(SearchActions.setShowAllData(false));

  let searchData = createSliderSearchData(sliderValue, proteinName);
  if (searchData === null) {
    return null;
  }

  let requestBody = new SearchRequestObject(searchData, filter);
  let searchType = "advanced";

  const searchResults = await getSearchRequestResponse(dispatch, searchType, requestBody, pageNumber, pageSize);
  await setSearchResults(dispatch, searchResults, history);

  dispatch(SearchActions.setShowResults(true));
  dispatch(SearchActions.setIsSearching(false));

  let searchItems = createUrlSliderQuery(dispatch, proteinName, sliderValue);
  const data = await codec.compress(searchItems);
  history.push({ pathname: "/results", search: `type=${searchType}`.concat(`&`).concat(data) });

}


/**
 * Fetch select options
 */
const getSearchItemOptions = async () => {
  try {
    const response = await fetch(`./v1/search/options`);
    await status(response);
    const data = await response.json();
    return data;
  }
  catch (error) {
    return Promise.reject();
  }
}

/**
 * Get dataset names for select items (datasets/interpro families/organism)
 * @param dispatch Dispatch
 */
async function setSearchSelectItemsOptions(dispatch: Dispatch) {
  const optionsResponse = await getSearchItemOptions();
  dispatch(SearchActions.setDatasetsOptions(optionsResponse.datasetNames));
  dispatch(SearchActions.setInterProFamiliesOptions(optionsResponse.interProFamiliesNames));
  dispatch(SearchActions.setOrganismOptions(optionsResponse.organismNames));
}

/**
 * Advanced search
 * @param dispatch Dispatch
 * @param searchItems search items
 * @param filter filter for results
 * @param history history
 * @param pageNumber page number
 * @param pageSize page size
 */
export async function fetchRequestData(dispatch: Dispatch, searchItems: SearchItemState[], filter, history, pageNumber: number, pageSize: number): Promise<any> {

  dispatch(SearchActions.setShowAdvancedSearch(true));
  dispatch(SearchActions.setSearchResultsAdvancedSearch(true));
  dispatch(SearchActions.setSearchResultsFullTextSearch(false));
  dispatch(SearchActions.setPageNumber(pageNumber));
  let requestBody = createPredicatesRequest(searchItems, filter);
  if (requestBody === null) {
    return;
  }
  dispatch(SearchActions.setIsSearching(true));
  let searchType = "advanced";
  const searchPage = await getSearchRequestResponse(dispatch, searchType, requestBody, pageNumber, pageSize);

  await setSearchResults(dispatch, searchPage, history);
  dispatch(SearchActions.setShowResults(true));
  dispatch(SearchActions.setIsSearching(false));
  dispatch(SearchActions.setShowAdvancedSearch(false));
  dispatch(SearchActions.setSearchItemsFetching(searchItems));

  const data = await codec.compress(searchItems);

  history.push({ pathname: "/results", search: `type=${searchType}`.concat(`&`).concat(data) });


}
/**
 * Set search items state for representing search all request
 * @param dispatch Dispatch
 */
function setSearchItemsAllData(dispatch: Dispatch) {
  dispatch(SearchActions.resetSearchItems());
  dispatch(SearchActions.setAllSearchItem());
}

/**
 * Reset search items state to default with one initial item
 * @param dispatch Dispatch
 */
function resetSearchItems(dispatch: Dispatch) {
  dispatch(SearchActions.resetSearchItems());
  dispatch(SearchActions.setSearchInitialItem());
}

/**
 * Fulltext search
 * @param dispatch Dispatch
 * @param searchQuery fulltext search query
 * @param filter filter for results
 * @param history history
 * @param pageNumber page number
 * @param pageSize page size
 */
export async function fetchFullTextData(dispatch: Dispatch, searchQuery: string, filter, history, pageNumber: number, pageSize: number) {

  dispatch(SearchActions.setPageNumber(0));
  dispatch(SearchActions.setSearchResultsAdvancedSearch(false));
  dispatch(SearchActions.setSearchResultsFullTextSearch(true));
  dispatch(SearchActions.setSearchResultsShowStatistics(false));
  dispatch(SearchActions.setIsSearchingFulltext(true));
  dispatch(SearchActions.setIsSearching(true));
  let searchRequest = new SearchRequestObject(searchQuery, filter);

  let searchType = "fulltext";
  const fullTextResultsPage = await getSearchRequestResponse(dispatch, searchType, searchRequest, pageNumber, pageSize);
  await setSearchResults(dispatch, fullTextResultsPage, history);
  dispatch(SearchActions.setShowResults(true));
  dispatch(SearchActions.setIsSearching(false));
  dispatch(SearchActions.setIsSearchingFulltext(false));

  history.push({ pathname: "/results", search: `type=${searchType}`.concat(`&`).concat(encodeURIComponent(searchQuery)) });

}


const mapStateToProps = (state: AppState, ownProps: any): SearchComponentPropsData => {
  const mappedState = state.searchState;
  return {
    classes: ownProps.classes,
    history: ownProps.history,
    query: ownProps.location.query,
    searchItems: mappedState.searchItems,
    addButtonIsShown: mappedState.addButtonIsShown,
    warnings: mappedState.warnings,
    showWarning: mappedState.showWarning,
    isSearching: mappedState.isSearching,
    isSearchingFulltext: mappedState.isSearchingFulltext,
    fullTextSearchValue: mappedState.fullTextSearchValue,
    showAdvancedSearch: mappedState.showAdvancedSearch,
    datasetsOptions: mappedState.datasetsOptions,
    searchItemsFetching: mappedState.searchItemsFetching,
    fullTextSearchValueFetching: mappedState.fullTextSearchValueFetching,
    showAllData: mappedState.showAllData,
    logicOperators: mappedState.logicOperators,
    interproFamiliesOptions: mappedState.interproFamiliesOptions
  };
}

const mapDispatchToProps = (dispatch: Dispatch): SearchComponentPropsFunctions => ({

  fetchFullTextData: (searchQuery, filter, history, pageNumber, pageSize) => fetchFullTextData(dispatch, searchQuery, filter, history, pageNumber, pageSize),
  fetchRequestData: (searchItems, filter, history, pageNumber, pageSize) => fetchRequestData(dispatch, searchItems, filter, history, pageNumber, pageSize),
  fetchAllRequestData: (filter, history, pageNumber, pageSize) => fetchAllRequestData(dispatch, filter, history, pageNumber, pageSize),
  fetchChartRequestData: (data, dataType, filter, history, pageNumber, pageSize) => fetchChartRequestData(dispatch, data, dataType, filter, history, pageNumber, pageSize),
  getSearchItemsOptions: () => setSearchSelectItemsOptions(dispatch),
  setSearchItemsFetching: (items) => dispatch(SearchActions.setSearchItemsFetching(items)),
  setFulltextValueFetching: (value) => dispatch(SearchActions.setFulltextValueFetching(value)),
  setLogicalOperator: (operator, id) => dispatch(SearchActions.setLogicalOperator(operator, id)),
  setSearchOption: (option, id) => dispatch(SearchActions.setSearchOption(option, id)),
  setSearchedValue: (searchValue, id) => dispatch(SearchActions.setSearchedValue(searchValue, id)),
  addSearchItem: () => dispatch(SearchActions.addSearchItem()),
  removeSearchItem: (id) => dispatch(SearchActions.removeSearchItem(id)),
  setIsSearching: (isSearching) => dispatch(SearchActions.setIsSearching(isSearching)),
  setIsSearchingFulltext: (isSearching) => dispatch(SearchActions.setIsSearchingFulltext(isSearching)),
  setAllSearchItem: () => setSearchItemsAllData(dispatch),
  setSearchFulltextValue: (value) => dispatch(SearchActions.setSearchFulltextValue(value)),
  resetSearch: () => resetSearchItems(dispatch),
  setShowAdvancedSearch: (show) => dispatch(SearchActions.setShowAdvancedSearch(show)),
  setShowAllData: (show) => dispatch(SearchActions.setShowAllData(show)),
  setSearchLogicOperator: (operator, id) => dispatch(SearchActions.setSearchLogicOperator(operator, id)),
  addSearchLogicOperator: (operator) => dispatch(SearchActions.addSearchLogicOperator(operator)),
  removeSearchLogicOperator: (id) => dispatch(SearchActions.removeSearchLogicOperator(id)),
  setShowResultsStatistics: (show) => dispatch(SearchActions.setSearchResultsShowStatistics(show))
})


const SearchContainerCreator = connect(
  mapStateToProps,
  mapDispatchToProps
);

export const SearchContainer = withRouter(SearchContainerCreator(SearchBar));
export const NavBarContainer = withRouter(SearchContainerCreator(NavBar));
export const FullTextSearchContainer = withRouter(SearchContainerCreator(FullTextSearch));
