-- DROP AND CREATE INDEXES
-- ---------------------------------------------------------------------------------------------------------------------
DROP INDEX IF EXISTS graph_id_idx;
CREATE INDEX graph_id_idx ON t_graph_attribute USING btree (graph_id ASC NULLS LAST) TABLESPACE pg_default;

DROP INDEX IF EXISTS node_id_idx;
CREATE INDEX node_id_idx ON t_node_attribute USING btree (node_id ASC NULLS LAST) TABLESPACE pg_default;

DROP INDEX IF EXISTS att_edge_id_idx;
CREATE INDEX att_edge_id_idx ON t_edge_attribute USING btree (edge_id ASC NULLS LAST) TABLESPACE pg_default;

DROP INDEX IF EXISTS sym_edge_id_idx;
CREATE INDEX sym_edge_id_idx ON t_edge_symbol USING btree (edge_id ASC NULLS LAST) TABLESPACE pg_default;

DROP INDEX IF EXISTS parent_id_idx;
CREATE INDEX parent_id_idx ON t_component USING btree (parent_id ASC NULLS LAST) TABLESPACE pg_default;