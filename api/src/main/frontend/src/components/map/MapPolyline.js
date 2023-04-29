import { useEffect, useRef, useState } from "react";
import { Polyline } from "react-leaflet";

export default function MapPolyLine(props) {
  const { trafficOriginMarker, databaseNodes, getIntermediatePoint } = props;
  const pathOptions = {
    color: "#404f59",
    dashArray: "3,5",
    weight: "4",
  };
  const [polyline, setPolyline] = useState({
    origin: trafficOriginMarker.coords,
    percentage: 0,
    points: [],
  });

  const intervalRef = useRef();
  useEffect(() => {
    if (databaseNodes?.nodes?.length === undefined) return;
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      setPolyline((prev) => {
        return {
          percentage: 0,
          points: [],
        };
      });
    }


    const destination =
      databaseNodes.nodes[databaseNodes.connectedNodeIndex].location;

    intervalRef.current = setInterval(() => {
      setPolyline(({ percentage, points }) => {
        const newCoords = getIntermediatePoint(
          trafficOriginMarker.coords[0],
          trafficOriginMarker.coords[1],
          destination.latitude,
          destination.longitude,
          percentage
        );
        points.push(newCoords);
        if (percentage + 5 > 105) {
          points = [];
          percentage = 0;
        } else {
          percentage = percentage + 5;
        }
        return { destination, points, percentage };
      });
    }, 200);
    return () => {
      clearInterval(intervalRef.current);
    };
  }, [databaseNodes, trafficOriginMarker]);

  return <Polyline pathOptions={pathOptions} positions={[polyline.points]} />;
}
