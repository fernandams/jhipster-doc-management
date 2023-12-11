import { IFolder } from 'app/shared/model/folder.model';

export interface IDocument {
  id?: number;
  title?: string;
  description?: string | null;
  dataContentType?: string;
  data?: string;
  uploaded?: string | null;
  folder?: IFolder | null;
}

export const defaultValue: Readonly<IDocument> = {};
