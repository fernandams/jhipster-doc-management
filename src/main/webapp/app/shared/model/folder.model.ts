export interface IFolder {
  id?: number;
  title?: string;
  description?: string | null;
  created?: string | null;
}

export const defaultValue: Readonly<IFolder> = {};
