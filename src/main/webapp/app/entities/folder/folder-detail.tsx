import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, byteSize, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './folder.reducer';

export const FolderDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const folderEntity = useAppSelector(state => state.folder.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="folderDetailsHeading">
          <Translate contentKey="docManagementApp.folder.detail.title">Folder</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{folderEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="docManagementApp.folder.title">Title</Translate>
            </span>
          </dt>
          <dd>{folderEntity.title}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="docManagementApp.folder.description">Description</Translate>
            </span>
          </dt>
          <dd>{folderEntity.description}</dd>
          <dt>
            <span id="created">
              <Translate contentKey="docManagementApp.folder.created">Created</Translate>
            </span>
          </dt>
          <dd>{folderEntity.created ? <TextFormat value={folderEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
        </dl>
        <Button tag={Link} to="/folder" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/folder/${folderEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default FolderDetail;
