import { createContext, useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import getJSON from "../services/rest";

const AppContext = createContext();

export function AppProvider({ children }) {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [queryLogs, setQueryLogs] = useState([]);
  //default path is /api, other paths include /api-asia, /api-europe, etc
  const [trafficLocations, setTrafficLocations] = useState([]);
  const [trafficLocation, setTrafficLocation] = useState(
    localStorage.hasOwnProperty("trafficLocation")
      ? localStorage.getItem("trafficLocation")
      : "Boston"
  );
  const [currentDatabase, setCurrentDatabase] = useState(
    localStorage.hasOwnProperty("currentDatabase")
      ? localStorage.getItem("currentDatabase")
      : 0
  );

  const [showExecutionPlan, setShowExecutionPlan] = useState(
    localStorage.hasOwnProperty("showExecPlan")? localStorage.getItem("showExecPlan"):false
  );
  const [databases, setDatabases] = useState([]);
  const [databaseNodes, setDatabaseNodes] = useState({});

  const getDatabases = async () => {
    try {
      const db = await getJSON("/api/refdata/dbtypes");

      setDatabases(db);
      return Promise.resolve();
    } catch (e) {
      console.log("error in fetching current database", e);
    }
  };

  const getTrafficLocations = async () => {
    try {

      //const json = await getJSON(`/${trafficLocation}/trafficLocations`);
      const json = await getJSON('/api/refdata/trafficLoc');
      // console.log('trafficLocations', json)
      setTrafficLocations(json);
      return Promise.resolve();
    } catch (e) {
      console.log("error in fetching /trafficLocations", e);
    }
  };

  const initialize = async () => {
    // console.log("Initializing traffic locations and databases");
    await Promise.all([getTrafficLocations(), getDatabases()]);
    setLoading(false);
  };
  useEffect(() => {
    initialize();
  }, []);

  useEffect(() => {
    getNodesForDB();

    // Navigate to products view on Traffic Location or Database change
    // if not on landing page or initial page load
    if (location?.pathname !=="/" && loading !== true)
      navigate("/dashboard", { replace: true });
  }, [trafficLocation, currentDatabase]);

  const getNodesForDB = async () => {
    try {
      const resp = await getJSON('/api/refdata/dbnodes');
      const bestNode = await getJSON('/api/refdata/optimalDBNode');
      const bestNodeIndex = resp.findIndex((node) => node.id === bestNode.id);


      const nodes = resp;
      const isReplicaNode =
        resp[0].nodeType === "replica";
      setDatabaseNodes({
        nodes,
        connectedNodeIndex: bestNodeIndex,
        isReplicaNode,
      });
    } catch (e) {
      console.log("error in fetching database nodes", e);
    }
  };

  const handleQueryLogs = (logs, explainAnalyzeResults, latency, connectionInfo) => {
    const logResult = {
      logs,
      explainAnalyzeResults,
      latency,
      connectionInfo
    };
    setQueryLogs((prev) => {
      if (prev.length > 30) {
        return [...prev.slice(prev.length - 30, prev.length), logResult];
      } else {
        return [...prev, logResult];
      }
    });
  };


  return (
    <AppContext.Provider
      value={{
        databases,
        setDatabases,
        databaseNodes,
        setDatabaseNodes,
        handleQueryLogs,
        queryLogs,
        loading,
        trafficLocation,
        setTrafficLocation,
        trafficLocations,
        setTrafficLocations,
        currentDatabase,
        setCurrentDatabase,
        showExecutionPlan,
        setShowExecutionPlan
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export default AppContext;
