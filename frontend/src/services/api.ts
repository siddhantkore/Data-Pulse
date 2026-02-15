import type { DocumentMetadata, PagedResponse } from "@/types";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

class ApiService {
  private async request<T>(
    endpoint: string,
    options?: RequestInit
  ): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`;
    const response = await fetch(url, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...options?.headers,
      },
    });

    if (!response.ok) {
      throw new Error(`API request failed: ${response.statusText}`);
    }

    return response.json();
  }

  async searchDocuments(query: string): Promise<DocumentMetadata[]> {
    return this.request<DocumentMetadata[]>(
      `/api/search/text-search?q=${encodeURIComponent(query)}`
    );
  }

  async getAllDocuments(
    page: number = 0,
    size: number = 20
  ): Promise<PagedResponse<DocumentMetadata>> {
    const response = await this.request<any>(
      `/api/search/get-all-docs?page=${page}&size=${size}`
    );
    
    // Handle Spring PagedModel structure - it might be wrapped in _embedded or have different structure
    // Spring Data Web PagedModel can have content directly or in _embedded.content
    let content: DocumentMetadata[] = [];
    let pageInfo: any = {};
    
    if (response._embedded && response._embedded.content) {
      // HAL format with _embedded
      content = response._embedded.content;
      pageInfo = response.page || {};
    } else if (Array.isArray(response.content)) {
      // Direct content array
      content = response.content;
      pageInfo = response;
    } else if (Array.isArray(response)) {
      // Sometimes it might just be an array
      content = response;
      return {
        content,
        pageable: { pageNumber: page, pageSize: size, sort: { sorted: false, unsorted: true, empty: false } },
        totalElements: content.length,
        totalPages: 1,
        last: true,
        first: true,
        size,
        number: page,
        numberOfElements: content.length,
        empty: content.length === 0,
      };
    }
    
    return {
      content,
      pageable: pageInfo.pageable || { pageNumber: pageInfo.number || page, pageSize: pageInfo.size || size, sort: { sorted: false, unsorted: true, empty: false } },
      totalElements: pageInfo.totalElements || content.length,
      totalPages: pageInfo.totalPages || 1,
      last: pageInfo.last !== undefined ? pageInfo.last : (pageInfo.totalPages === undefined ? true : (pageInfo.number || page) >= (pageInfo.totalPages || 1) - 1),
      first: pageInfo.first !== undefined ? pageInfo.first : (pageInfo.number || page) === 0,
      size: pageInfo.size || size,
      number: pageInfo.number !== undefined ? pageInfo.number : page,
      numberOfElements: pageInfo.numberOfElements !== undefined ? pageInfo.numberOfElements : content.length,
      empty: pageInfo.empty !== undefined ? pageInfo.empty : content.length === 0,
    };
  }

  async uploadDocument(file: File): Promise<DocumentMetadata> {
    const formData = new FormData();
    formData.append("file", file);

    const response = await fetch(`${API_BASE_URL}/api/documents/upload`, {
      method: "POST",
      body: formData,
    });

    if (!response.ok) {
      throw new Error(`Upload failed: ${response.statusText}`);
    }

    return response.json();
  }

  async downloadDocument(s3Key: string, fileName: string): Promise<void> {
    const response = await fetch(
      `${API_BASE_URL}/api/documents/download/${encodeURIComponent(s3Key)}`
    );

    if (!response.ok) {
      throw new Error(`Download failed: ${response.statusText}`);
    }

    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  }

  async pullEmails(): Promise<string> {
    return this.request<string>("/api/documents/pull");
  }

  async deleteDocument(id: string): Promise<{ success: boolean; message: string }> {
    const response = await fetch(`${API_BASE_URL}/api/documents/${encodeURIComponent(id)}`, {
      method: "DELETE",
    });

    const result = await response.json();

    if (!response.ok || !result.success) {
      throw new Error(result.message || `Delete failed: ${response.statusText}`);
    }

    return result;
  }

  async getCategories(): Promise<Map<string, number>> {
    const response = await this.request<Record<string, number>>("/api/search/categories");
    return new Map(Object.entries(response));
  }
}

export const apiService = new ApiService();

