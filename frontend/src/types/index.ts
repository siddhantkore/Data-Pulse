export interface DocumentMetadata {
  id: string;
  fileName: string;
  s3Key: string;
  extractedText?: string;
  categories: string[];
  keywords: string[];
  metadata?: {
    title?: string;
    authorOrSender?: string;
    date?: string;
    entities?: string[];
    summary?: string;
  } | null;
  documentSpecificFields?: Record<string, any>;
  documentStatus?: string;
  createdAtLocalDateTime?: string;
  updatedAtLocalDateTime?: string;
}

export interface PagedResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      unsorted: boolean;
      empty: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  size: number;
  number: number;
  numberOfElements: number;
  empty: boolean;
}

export interface Category {
  id: string;
  name: string;
  documentCount: number;
}

export interface CategoriesResponse {
  [categoryName: string]: number;
}

