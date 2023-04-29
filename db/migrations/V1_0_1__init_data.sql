truncate table REF_DATA;

insert into REF_DATA(KEY_NAME, KEY_VALUE) values ('TRAFFIC_LOCATIONS',
'{"TRAFFIC_LOCATIONS":[
 {
  "id": 1,
  "name":"Boston",
  "country":"United States",
  "latitude":"42.36",
  "longitude": "-71.05"
 },
 {
  "id": 2,
  "name":"Washington",
  "country":"United States",
  "latitude":"47.751",
  "longitude": "-120.740"
 },
{
  "id": 3,
  "name":"London",
  "country":"United Kingdom",
  "latitude":"51.509",
  "longitude": "-0.118"
 },
{
  "id": 4,
  "name":"Mumbai",
  "country":"India",
  "latitude":"19.076",
  "longitude": "72.877"
 },
{
  "id": 5,
  "name":"Sydney",
  "country":"Australia",
  "latitude":"-33.865",
  "longitude": "151.209"
 }
]
}');

insert into REF_DATA(KEY_NAME, KEY_VALUE) values( 'DB_CLUSTER_TYPES',
'{
	"DB_CLUSTER_TYPES": [
	    {
	        "id":0,
			"title": "Single-region, multi-zone",
			"subtitle": "3 node deployed in US West"
		},
		{
		    "id":1,
			"title": " Multi-region",
			"subtitle": "3 nodes deployed in US West, US Central and US East"
		},
		{
		  "id":2,
			"title": " Multi-region, multi-zone with Read Replicas",
			"subtitle": "3 nodes deployed in US East, with read replicas in Europe and Asia"
		},
		{
			"id":3,
			"title": " Geo-partitioned",
			"subtitle": "3 nodes deployed in US East, with 2 nodes in Europe and Asia"
		}
    ]
}'
);

insert into REF_DATA(KEY_NAME, KEY_VALUE) values( 'DEFAULT_NODE_LOCATIONS',
'{
	"DEFAULT_NODE_LOCATIONS": [
	    {
		  "id": 0,
		  "name":"useast1",
		  "country":"USA",
		  "latitude":"38.13",
		  "longitude": "-78.45"
		 },
		 {
		  "id": 1,
		  "name":"useast2",
		  "country":"USA",
		  "latitude":"39.96",
		  "longitude": "-83"
		 },
		 {
		  "id": 2,
		  "name":"uswest1",
		  "country":"USA",
		  "latitude":"37.35",
		  "longitude": "-121.96"
		 },
		 {
		  "id": 3,
		  "name":"uswest2",
		  "country":"USA",
		  "latitude":"46.15",
		  "longitude": "-123.88"
		 },
		 {
		  "id": 4,
		  "name":"euwest1",
		  "country":"Ireland",
		  "latitude":"53",
		  "longitude": "-8"
		 },
		 {
		  "id": 5,
		  "name":"euwest2",
		  "country":"UK",
		  "latitude":"51",
		  "longitude": "-0.1"
		 },
		 {
		  "id": 6,
		  "name":"euwest3",
		  "country":"FRANCE",
		  "latitude":"48.86",
		  "longitude": "2.35"
		 },
		 {
		  "id": 7,
		  "name":"eucentral1",
		  "country":"GERMANY",
		  "latitude":"50",
		  "longitude": "8"
		 },
		 {
		  "id": 8,
		  "name":"apsoutheast1",
		  "country":"SINGAPORE",
		  "latitude":"1.32",
		  "longitude": "103.69"
		 },
		{
		  "id": 9,
		  "name":"apsoutheast2",
		  "country":"AUSTRALIA",
		  "latitude":"-33.91",
		  "longitude": "151.19"
		 },
		{
		  "id": 10,
		  "name":"apsouth1",
		  "country":"INDIA",
		  "latitude":"19.242",
		  "longitude": "72.96"
		 },
		{
		  "id": 11,
		  "name":"apnortheast1",
		  "country":"JAPAN",
		  "latitude":"35.617",
		  "longitude": "139.74"
		 },
		 {
		  "id": 12,
		  "name":"datacenter1",
		  "country":"USA",
		  "latitude":"38.13",
		  "longitude": "-78.45"
		 }
    ]
}'
);

commit;
