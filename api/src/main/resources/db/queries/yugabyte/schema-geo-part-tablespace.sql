-- us-east-1 us-east-2 us-west-1 us-west-2

CREATE TABLESPACE west_tablespace WITH (
  replica_placement='{"num_replicas": 3, "placement_blocks":
  [{"cloud":"aws","region":"us-west-1","zone":"us-west-1a","min_num_replicas":1,"leader_preference":1},
  {"cloud":"aws","region":"us-west-2","zone":"us-west-2a","min_num_replicas":1,"leader_preference":2},
  {"cloud":"aws","region":"us-east-1","zone":"us-west-1a","min_num_replicas":1}]}'
);

CREATE TABLESPACE east_tablespace WITH (
  replica_placement='{"num_replicas": 3, "placement_blocks":
  [{"cloud":"aws","region":"us-east-1","zone":"us-east-1a","min_num_replicas":1,"leader_preference":1},
  {"cloud":"aws","region":"us-east-2","zone":"us-east-2a","min_num_replicas":1,"leader_preference":2},
  {"cloud":"aws","region":"us-west-1","zone":"us-west-1a","min_num_replicas":1}
  ]}'
);

