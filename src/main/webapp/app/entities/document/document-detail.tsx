import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, openFile, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './document.reducer';

export const DocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const documentEntity = useAppSelector(state => state.document.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="documentDetailsHeading">
          <Translate contentKey="docManagementApp.document.detail.title">Document</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{documentEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="docManagementApp.document.title">Title</Translate>
            </span>
          </dt>
          <dd>{documentEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="docManagementApp.document.description">Description</Translate>
            </span>
          </dt>
          <dd>{documentEntity.description}</dd>
          <dt>
            <span id="data">
              <Translate contentKey="docManagementApp.document.data">Data</Translate>
            </span>
          </dt>
          <dd>
            {documentEntity.data ? (
              <div>
                {documentEntity.dataContentType ? (
                  <a onClick={openFile(documentEntity.dataContentType, documentEntity.data)}>
                    <Translate contentKey="entity.action.open">Open</Translate>&nbsp;
                  </a>
                ) : null}
                <span>
                  {documentEntity.dataContentType}, {byteSize(documentEntity.data)}
                </span>
              </div>
            ) : null}
          </dd>
          <dt>
            <span id="uploaded">
              <Translate contentKey="docManagementApp.document.uploaded">Uploaded</Translate>
            </span>
          </dt>
          <dd>{documentEntity.uploaded ? <TextFormat value={documentEntity.uploaded} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="docManagementApp.document.folder">Folder</Translate>
          </dt>
          <dd>{documentEntity.folder ? documentEntity.folder.title : ''}</dd>
        </dl>
        <Button tag={Link} to="/document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/document/${documentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DocumentDetail;
