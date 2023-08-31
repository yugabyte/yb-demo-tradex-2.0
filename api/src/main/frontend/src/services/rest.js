// options: { logger: logHandlerFunction }

function buildHeaders(myDBSelection, myLocation) {

  const reqHeaders = new Headers();
  reqHeaders.append('Content-Type', 'application/json');
  reqHeaders.append('Accept','application/json');

  switch(myDBSelection) {
        case '0':
          reqHeaders.append('X-TRADEX-DB-TYPE', 'SINGLE_REGION_MULTI_ZONE');
          break;
        case '1':
          reqHeaders.append('X-TRADEX-DB-TYPE','MULTI_REGION_MULTI_ZONE');
          break;
        case '2':
          reqHeaders.append('X-TRADEX-DB-TYPE','MULTI_REGION_READ_REPLICA');
          break;
        case '3':
          reqHeaders.append('X-TRADEX-DB-TYPE', 'GEO_PARTITIONED');
          break;
        default:
          reqHeaders.append('X-TRADEX-DB-TYPE', 'MULTI_REGION_MULTI_ZONE');
          break;
  }

  reqHeaders.append('X-USER-LOCATION', myLocation);
  let authToken = window.localStorage.getItem('authToken');

  if ( authToken !== undefined && authToken !== '') {
    reqHeaders.append('Authorization', "Bearer " + authToken);
  }

  let showExecDetails = window.localStorage.getItem('showExecPlan');
  
  if ( showExecDetails !== undefined && showExecDetails !== '' && null !== showExecDetails){
    reqHeaders.append('X-TRADEX-QUERY-ANALYZE', showExecDetails)
  }

  return reqHeaders;
}


export default async function getJSON(baseURL, options) {
  try {

  let targetUrl = baseURL;
  let myLocation = localStorage.hasOwnProperty("trafficLocation") ? localStorage.getItem("trafficLocation") : "Boston";
  if ( window.location.hostname.includes('yr-tradex.aws.ats-yb.ga')) {
   // const trafficLocation = localStorage.hasOwnProperty("trafficLocation") ? localStorage.getItem("trafficLocation") : "Boston";
    if ( !window.location.hostname.includes(myLocation) ) {
        targetUrl = 'https://' + myLocation.toLowerCase() + '-yr-tradex.aws.ats-yb.ga' + baseURL;
    }
  }

  const dbSelection = localStorage.hasOwnProperty("currentDbSelection") ? localStorage.getItem("currentDbSelection") : 1;
  const getHeaders = buildHeaders(dbSelection, myLocation); //new Headers();
  

    if (options) {
      for (const key in options) {
        targetUrl.searchParams.append(key, options[key]);
      }
    }
    const res = await fetch(targetUrl, {
      headers: getHeaders,
    });
    const json = await res.json();
    return json;
  } catch (e) {
    console.log(e);
    throw new Error(e);
  }
}

// Example POST method implementation:
export async function postJSON(baseURL = "", data = {}, options) {
  // Default options are marked with *
  try {
    let targetUrl = baseURL;
      let myLocation = localStorage.hasOwnProperty("trafficLocation") ? localStorage.getItem("trafficLocation") : "Boston";
      if ( window.location.hostname.includes('yr-tradex.aws.ats-yb.ga')) {
        //const trafficLocation = localStorage.hasOwnProperty("trafficLocation") ? localStorage.getItem("trafficLocation") : "Boston";
        if ( !window.location.hostname.includes(myLocation) ) {
            targetUrl = 'https://' + myLocation.toLowerCase() + '-yr-tradex.aws.ats-yb.ga' + baseURL;
        }
      }

    const dbSelection = localStorage.hasOwnProperty("currentDbSelection") ? localStorage.getItem("currentDbSelection") : 1;
    const postHeaders = buildHeaders(dbSelection, myLocation);   


    if (options) {
      for (const key in options) {
        targetUrl.searchParams.append(key, options[key]);
      }
    }
    const response = await fetch(targetUrl, {
      method: "POST", // *GET, POST, PUT, DELETE, etc.
      mode: "cors", // no-cors, *cors, same-origin
      cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
      credentials: "same-origin", // include, *same-origin, omit
      headers: postHeaders,
      redirect: "follow", // manual, *follow, error
      referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
      body: JSON.stringify(data), // body data type must match "Content-Type" header
    }).then( (res) => {      
       return res.json();      
    }).catch( (err) => {
      console.log('Got error while posting', err);
    });
    return response; // parses JSON response into native JavaScript objects
  } catch (e) {
    console.log(e);
    throw new Error(e);
  }
}

export async function putJSON(baseURL = "", data = {}, options) {
  // Default options are marked with *
  try {
    let targetUrl = baseURL;
      console.log('baseUrl', baseURL);
      let myLocation = localStorage.hasOwnProperty("trafficLocation") ? localStorage.getItem("trafficLocation") : "Boston";
      if ( window.location.hostname.includes('yr-tradex.aws.ats-yb.ga')) {
        //const trafficLocation = localStorage.hasOwnProperty("trafficLocation") ? localStorage.getItem("trafficLocation") : "Boston";
        if ( !window.location.hostname.includes(myLocation) ) {
            targetUrl = 'https://' + myLocation.toLowerCase() + '-yr-tradex.aws.ats-yb.ga' + baseURL;
        }
      }

    const dbSelection = localStorage.hasOwnProperty("currentDbSelection") ? localStorage.getItem("currentDbSelection") : 1;
    const putHeaders = buildHeaders(dbSelection, myLocation);     


    if (options) {
      for (const key in options) {
        targetUrl.searchParams.append(key, options[key]);
      }
    }
    const response = await fetch(targetUrl, {
      method: "PUT", // *GET, POST, PUT, DELETE, etc.
      mode: "cors", // no-cors, *cors, same-origin
      cache: "no-cache", // *default, no-cache, reload, force-cache, only-if-cached
      credentials: "same-origin", // include, *same-origin, omit
      headers: putHeaders,
      redirect: "follow", // manual, *follow, error
      referrerPolicy: "no-referrer", // no-referrer, *no-referrer-when-downgrade, origin, origin-when-cross-origin, same-origin, strict-origin, strict-origin-when-cross-origin, unsafe-url
      body: JSON.stringify(data), // body data type must match "Content-Type" header
    });
    return response.json(); // parses JSON response into native JavaScript objects
  } catch (e) {
    console.log(e);
    throw new Error(e);
  }
}
