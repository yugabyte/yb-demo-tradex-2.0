CREATE TABLESPACE london_tablespace WITH (
  replica_placement='{"num_replicas": 2, "placement_blocks":
  [{"cloud":"aws","region":"eu-west-2","zone":"eu-west-2a","min_num_replicas":1},
  {"cloud":"aws","region":"us-west-2","zone":"us-west-2a","min_num_replicas":1}]}'
);

CREATE TABLESPACE mumbai_tablespace WITH (
  replica_placement='{"num_replicas": 2, "placement_blocks":
  [{"cloud":"aws","region":"ap-south-1","zone":"ap-south-1a","min_num_replicas":1},
  {"cloud":"aws","region":"eu-west-2","zone":"eu-west-2a","min_num_replicas":1}
  ]}'
);

CREATE TABLESPACE sydney_tablespace WITH (
  replica_placement='{"num_replicas": 2, "placement_blocks":
  [{"cloud":"aws","region":"ap-southeast-2","zone":"ap-southeast-2a","min_num_replicas":1},
  {"cloud":"aws","region":"ap-south-1","zone":"ap-south-1a","min_num_replicas":1}]}'
);

CREATE TABLESPACE washington_tablespace WITH (
  replica_placement='{"num_replicas": 2, "placement_blocks":
  [{"cloud":"aws","region":"us-west-2","zone":"us-west-2a","min_num_replicas":1},
  {"cloud":"aws","region":"us-east-1","zone":"us-east-1a","min_num_replicas":1}]}'
);

CREATE TABLESPACE boston_tablespace WITH (
  replica_placement='{"num_replicas": 2, "placement_blocks":
  [{"cloud":"aws","region":"us-east-1","zone":"us-east-1a","min_num_replicas":1},
  {"cloud":"aws","region":"us-west-2","zone":"us-west-2a","min_num_replicas":1}]}'
);
