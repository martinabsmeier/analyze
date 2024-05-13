-- DROP AND CREATE TABLES
-- ---------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS t_graph CASCADE;
CREATE TABLE t_graph
(
    id       INTEGER PRIMARY KEY     NOT NULL,
    type     INTEGER                 NOT NULL,
    name     CHARACTER VARYING(2048) NOT NULL,
    path     CHARACTER VARYING(2048),
    checksum char(32)
) WITH ( OIDS= FALSE );
ALTER TABLE t_graph
    OWNER TO postgres;

DROP TABLE IF EXISTS t_graph_attribute CASCADE;
CREATE TABLE t_graph_attribute
(
    id        INTEGER PRIMARY KEY             NOT NULL,
    graph_id  INTEGER REFERENCES t_graph (id) NOT NULL,
    att_key   CHARACTER VARYING(4096)          NOT NULL,
    att_value CHARACTER VARYING(65536)         NOT NULL
) WITH ( OIDS= FALSE );
ALTER TABLE t_graph_attribute
    OWNER TO postgres;

DROP TABLE IF EXISTS t_node CASCADE;
CREATE TABLE t_node
(
    id                 INTEGER PRIMARY KEY             NOT NULL,
    graph_id           INTEGER REFERENCES t_graph (id) NOT NULL,
    type               INTEGER                         NOT NULL,
    name               CHARACTER VARYING(512)          NOT NULL,
    operator_type      INTEGER,
    corresponding_node INTEGER,
    source_code_id     INTEGER,
    rank               INTEGER
) WITH ( OIDS= FALSE );
ALTER TABLE t_node
    OWNER TO postgres;

DROP TABLE IF EXISTS t_node_attribute CASCADE;
CREATE TABLE t_node_attribute
(
    id        INTEGER PRIMARY KEY            NOT NULL,
    node_id   INTEGER REFERENCES t_node (id) NOT NULL,
    att_key   CHARACTER VARYING(4096)        NOT NULL,
    att_value CHARACTER VARYING(65536)       NOT NULL
) WITH ( OIDS= FALSE );
ALTER TABLE t_node_attribute
    OWNER TO postgres;

DROP TABLE IF EXISTS t_edge CASCADE;
CREATE TABLE t_edge
(
    id           INTEGER PRIMARY KEY             NOT NULL,
    graph_id     INTEGER REFERENCES t_graph (id) NOT NULL,
    from_node_id INTEGER                         NOT NULL,
    to_node_id   INTEGER                         NOT NULL,
    type         INTEGER                         NOT NULL,
    name         CHARACTER VARYING(512)          NOT NULL,
    flow_type    INTEGER
) WITH ( OIDS= FALSE );
ALTER TABLE t_edge
    OWNER TO postgres;

DROP TABLE IF EXISTS t_edge_attribute CASCADE;
CREATE TABLE t_edge_attribute
(
    id        INTEGER PRIMARY KEY            NOT NULL,
    edge_id   INTEGER REFERENCES t_edge (id) NOT NULL,
    att_key   CHARACTER VARYING(4096)        NOT NULL,
    att_value CHARACTER VARYING(65536)       NOT NULL
) WITH ( OIDS= FALSE );
ALTER TABLE t_edge_attribute
    OWNER TO postgres;

DROP TABLE IF EXISTS t_edge_symbol CASCADE;
CREATE TABLE t_edge_symbol
(
    id                 INTEGER PRIMARY KEY            NOT NULL,
    edge_id            INTEGER REFERENCES t_edge (id) NOT NULL,
    associated_node_id INTEGER,
    node_id            INTEGER                        NOT NULL,
    type               INTEGER                        NOT NULL,
    att_key            CHARACTER VARYING(4096),
    att_value          CHARACTER VARYING(65536)
) WITH ( OIDS= FALSE );
ALTER TABLE t_edge_symbol
    OWNER TO postgres;

DROP TABLE IF EXISTS t_library CASCADE;
CREATE TABLE t_library
(
    id           INTEGER PRIMARY KEY    NOT NULL,
    component_id INTEGER                NOT NULL,
    type         INTEGER                NOT NULL,
    group_id     CHARACTER VARYING(128) NOT NULL,
    artifact_id  CHARACTER VARYING(128) NOT NULL,
    version      CHARACTER VARYING(54)  NOT NULL,
    revision_id  CHARACTER VARYING(128)
) WITH ( OIDS= FALSE );
ALTER TABLE t_library
    OWNER TO postgres;

DROP TABLE IF EXISTS t_application CASCADE;
CREATE TABLE t_application
(
    id           INTEGER PRIMARY KEY     NOT NULL,
    component_id INTEGER                 NOT NULL,
    type         INTEGER                 NOT NULL,
    name         CHARACTER VARYING(2048) NOT NULL,
    revision_id  CHARACTER VARYING(128)
) WITH ( OIDS= FALSE );
ALTER TABLE t_application
    OWNER TO postgres;

DROP TABLE IF EXISTS t_component CASCADE;
CREATE TABLE t_component
(
    id         INTEGER PRIMARY KEY      NOT NULL,
    type       JSON                     NOT NULL,
    value      CHARACTER VARYING(65536) NOT NULL,
    checksum   CHARACTER(32),
    graph_id   INTEGER,
    parent_id  INTEGER,
    attributes JSON
) WITH ( OIDS= FALSE );
ALTER TABLE t_component
    OWNER TO postgres;
