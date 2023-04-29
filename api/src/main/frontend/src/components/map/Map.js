import { useContext, useEffect, useMemo, useRef, useState } from "react";
import AppContext from "../../contexts/AppContext";
import { v4 as uuidv4 } from "uuid";
import {
  FeatureGroup,
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  CircleMarker,
  ZoomControl,
} from "react-leaflet";
import L from "leaflet";
import { makeStyles } from "@material-ui/core";
import MapPolyLine from "./MapPolyline";
import OriginLocationLegendIcon from "./ellipse.svg";
import OriginLocationIcon from "./origin-location.svg";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import "./map.css";


const useStyles = makeStyles((theme) => {
  return {
    mapWrapper: {
      position: "relative",
      overflow: "hidden",
      borderRadius: "0 0 10px 10px",
      
      "& .leaflet-div-icon": {
        background: "none",
        border: "none",
        pointerEvents: "none",
      },
    },
    map: {
      height: "350px",
      flex: "1 1 auto",
    },
    snapToZoom: {
      position: "absolute",
      top: "10px",
      right: "10px",
      zIndex: 999,
      border: "2px solid rgba(0,0,0,0.2)",
      borderRadius: "4px",
      boxShadow: "none",

      "& a": {
        height: "30px",
        width: "30px",
      },
    },
    legend: {
      display: "flex",
      position: "absolute",
      bottom: "-1px",
      left: "-1px",
      zIndex: 9999,
      gap: "5px",
      alignItems: "center",
      backgroundColor: "rgba(255,255,255, 0.7)",
      borderTop: "1px solid #E9EEF2",
      borderRight: "1px solid #E9EEF2",
      padding: "5px",
      fontFamily: '"Inter", sans-serif',
      fontStyle: "normal",
      fontSize: "11px",
      fontWeight: 500,
      lineHeight: "7px",
      letterSpacing: "0px",
      border: "1px solid #D7DEE4",
      borderRadius: "0 0 0 10px",
    },
  };
});





// mathematical simplification, should be using radians if precision is required
function getIntermediatePoint(lat1, long1, lat2, long2, per) {
  per = per / 100;  
  return [lat1 + (lat2 - lat1) * per, long1 + (long2 - long1) * per];
}
export default function Map() {
  const classes = useStyles();
  const {
 
    databaseNodes,
    trafficLocation,
    trafficLocations,
  } = useContext(AppContext);
  const [trafficOriginMarker, setTrafficOriginMarker] = useState({
    coords: [51.505, -0.09],
    radius: 5,
  });
  const [mapCenter, setMapCenter] = useState([51.505, -0.09]);
  const [map, setMap] = useState(null);
  const connectionRef = useRef();
  const allNodesRef = useRef();

  const MarkerWithText = ({ text, position, textColor }) => {
    const html = `<div style="width: 100px; background: none; position: relative; left: 20px; bottom: 5px; color: ${textColor}; display:${
      text ? "" : "none"
    };">${text}</div>`;
    const textIcon = L.divIcon({ html: html });

    return <Marker position={position} icon={textIcon} />;
  };
  useEffect(() => {
    if (map) {
      map.fitBounds(allNodesRef.current.getBounds().pad(0.1));
    }
  }, [map]);

  useEffect(() => {
    setTrafficOriginMarker((prev) => {
      if ( undefined !== prev ){
        const location = trafficLocations.find(          
          (loc) => loc.name === trafficLocation
        );

        return { ...prev, coords: [location.latitude, location.longitude], label: location.name };
      }      
    });
  }, [trafficLocation]);

  useEffect(() => {

    if (databaseNodes?.nodes?.length === undefined) return;

    if (map) {
      map.fitBounds(allNodesRef.current.getBounds().pad(0.1));
    }


    const destination =  databaseNodes.nodes[databaseNodes.connectedNodeIndex].location;
    const intermediatePoint = getIntermediatePoint(
      trafficOriginMarker.latitude,
      trafficOriginMarker.longitude,
      destination.latitude,
      destination.longitude,
      50
    );
    setMapCenter(intermediatePoint);
  }, [databaseNodes, trafficOriginMarker]);

  const originLocationIcon = useMemo(() =>
    L.icon({
      iconSize: [19, 25],
      iconAnchor: [12, 12],
      iconUrl: OriginLocationIcon,
    })
  );

  const handleSnapToZoom = (e) => {
    e.preventDefault();
    if (map) map.fitBounds(connectionRef.current.getBounds().pad(0.1));
  };

  return (
    
    <div className={classes.mapWrapper}>
      <MapContainer
        center={mapCenter}
        zoom={1}
        zoomControl={false}
        scrollWheelZoom={true}
        className={classes.map}
        whenCreated={setMap}
      >
        <ZoomControl position="bottomright"></ZoomControl>
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
          url="https://{s}.basemaps.cartocdn.com/light_all/{z}/{x}/{y}{r}.png"
        />
        <FeatureGroup ref={allNodesRef}>
          <FeatureGroup ref={connectionRef} key={"connectionRefKey"}>
            {trafficOriginMarker && (
              <div key={uuidv4()}>
                <Marker
                  key={uuidv4()}
                  position={trafficOriginMarker.coords }
                  icon={originLocationIcon}
                >
                 
                </Marker>
                <MarkerWithText
                  key={uuidv4()}
                  text={trafficOriginMarker.label}
                  position={trafficOriginMarker.coords }
                  textColor={"#5D5FEF"}
                />
              </div>
            )}

            {databaseNodes &&
              databaseNodes.nodes &&
              databaseNodes.nodes
                .filter((node, idx) => idx === databaseNodes.connectedNodeIndex)
                .map((node) => {
                  return (
                    <div key={uuidv4()}>
                      <CircleMarker
                        center={[node.location.latitude,node.location.longitude]}
                        radius={5}
                        color={node.nodeType === "read_replica" ? "#ED35C5" : "#13A868"}
                        opacity="0.2"
                        weight="10"
                        fill="true"
                        fillColor={
                          node.nodeType === "read_replica" ? "#ED35C5" : "#13A868"
                        }
                        fillOpacity="1"
                      >
                      
                      </CircleMarker>
                      <MarkerWithText
                        text={node.label}
                        position={[node.location.latitude,node.location.longitude]}
                        textColor={
                          node.nodeType === "read_replica" ? "#ED35C5" : "#13A868"
                        }
                      />
                    </div>
                  );
                })}
          </FeatureGroup>
          {databaseNodes &&
            databaseNodes.nodes &&
            databaseNodes.nodes
              .filter((node, idx) => idx != databaseNodes.connectedNodeIndex)
              .map((node) => {
                return (
                  <div key={uuidv4()}  >
                    <CircleMarker
                      center={[node.location.latitude,node.location.longitude]}
                      radius={5}
                      color={node.nodeType === "read_replica" ? "#ED35C5" : "#13A868"}
                      opacity="0.2"
                      weight="10"
                      fill="true"
                      fillColor={
                        node.nodeType === "read_replica" ? "#ED35C5" : "#13A868"
                      }
                      fillOpacity="1"
                    >
                      <Popup>
                      <TableContainer sx={{width:"550px"}}>
      <Table  aria-label="simple table">
        <TableHead >
          <TableRow>
            <TableCell >Node Name</TableCell>
            <TableCell >Region</TableCell>
            <TableCell>Zone</TableCell>
         
          </TableRow>
        </TableHead>
        <TableBody>
          
            <TableRow
              key={node.id}
              sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
            >
              <TableCell component="th" scope="row" sx={{width:"10px"}}>
                {node.location.name}
              </TableCell>
              <TableCell sx={{width:"95px"}} > <img src="https://yb-global-tradex-imgstore.s3.amazonaws.com/img/apple.png" alt="USA" style={{width:"20px",height:"20px",marginRight:"6px"}} /> {node.region}</TableCell>
              <TableCell sx={{width:"10px"}}>{node.zone}</TableCell>
            
            </TableRow>
         
        </TableBody>
      </Table>
    </TableContainer>
                      </Popup>
                    </CircleMarker>
                    <MarkerWithText
                      text={node.label}
                      position={[node.location.latitude,node.location.longitude]}
                      textColor={
                        node.nodeType === "read_replica" ? "#ED35C5" : "#13A868"
                      }
                    />
                  </div>
                );
              })}
        </FeatureGroup>
        <MapPolyLine
          databaseNodes={databaseNodes}
          trafficOriginMarker={trafficOriginMarker}
          getIntermediatePoint={getIntermediatePoint}
        />
      </MapContainer>
      <div className={classes.snapToZoom}>
        <div className="leaflet-control leaflet-bar">
          <a
            href="#"
            onClick={(e) => {
              handleSnapToZoom(e);
            }}
          ></a>
        </div>
      </div>
      <div className={classes.legend}>
        <img src={OriginLocationLegendIcon} height={15} width={10} />
        <div>Phone Location</div>
        <div
          style={{
            height: "10px",
            width: "10px",
            borderRadius: "20px",
            backgroundColor: "#13a868",
          }}
        ></div>
        <div>Primary Node</div>
        <div
          style={{
            height: "10px",
            width: "10px",
            borderRadius: "20px",
            backgroundColor: "#ED35C5",
          }}
        ></div>
        <div>Read Replica</div>
      </div>
    </div>
  );
}
