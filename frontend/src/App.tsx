import { useState, useEffect, useMemo, useCallback, useRef } from "react";
import { AnimatePresence } from "framer-motion";
import { Sidebar } from "@/components/Sidebar";
import { Header } from "@/components/Header";
import { DocumentView } from "@/components/DocumentView";
import { MetadataSidebar } from "@/components/MetadataSidebar";
import { UploadDialog } from "@/components/UploadDialog";
import { Toaster } from "@/components/ui/toaster";
import { apiService } from "@/services/api";
import { useToast } from "@/hooks/use-toast";
import type { DocumentMetadata, Category } from "@/types";

function App() {
  const [documents, setDocuments] = useState<DocumentMetadata[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [activeCategory, setActiveCategory] = useState<string>("all");
  const [selectedDocument, setSelectedDocument] = useState<DocumentMetadata | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOption, setSortOption] = useState("date-desc");
  const [viewMode, setViewMode] = useState<"grid" | "list">("grid");
  const [isUploadOpen, setIsUploadOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoadingCategories, setIsLoadingCategories] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const { toast } = useToast();
  const isInitialMount = useRef(true);

  // Fetch all documents
  const fetchDocuments = useCallback(async (page: number = 0) => {
    try {
      setIsLoading(true);
      const response = await apiService.getAllDocuments(page, 20);
      const docs = response.content || [];
      if (page === 0) {
        setDocuments(docs);
      } else {
        setDocuments((prev) => [...prev, ...docs]);
      }
      setHasMore(!response.last);
      setCurrentPage(page);
    } catch (error) {
      console.error("Failed to fetch documents:", error);
      toast({
        title: "Error",
        description: "Failed to load documents. Please try again.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  }, []);

  // Fetch categories from backend (fallback to empty)
  const fetchCategories = useCallback(async () => {
    try {
      setIsLoadingCategories(true);
      const categoriesMap = await apiService.getCategories();
      const categoriesList: Category[] = Array.from(categoriesMap.entries()).map(
        ([name, count], index) => ({
          id: `cat-${index + 1}`,
          name,
          documentCount: count,
        })
      );
      setCategories(categoriesList);
    } catch (error) {
      console.error("Failed to fetch categories:", error);
      // Silently fail - categories will be empty
      setCategories([]);
    } finally {
      setIsLoadingCategories(false);
    }
  }, []);

  // Initial load
  useEffect(() => {
    (async () => {
      await fetchDocuments(0);
      await fetchCategories();
      isInitialMount.current = false;
    })();
  }, [fetchDocuments, fetchCategories]);

  // Debounced search
  useEffect(() => {
    // Skip on initial mount to avoid duplicate fetch
    if (isInitialMount.current) {
      return;
    }

    const debounceTimer = setTimeout(async () => {
      if (searchTerm.trim()) {
        try {
          setIsLoading(true);
          const results = await apiService.searchDocuments(searchTerm);
          setDocuments(results);
          setHasMore(false);
        } catch (error) {
          console.error("Failed to search documents:", error);
          toast({
            title: "Search Error",
            description: "Failed to search documents. Please try again.",
            variant: "destructive",
          });
        } finally {
          setIsLoading(false);
        }
      } else {
        fetchDocuments(0);
      }
    }, 500);

    return () => clearTimeout(debounceTimer);
  }, [searchTerm, toast, fetchDocuments]);

  const handleSelectDocument = (doc: DocumentMetadata) => {
    setSelectedDocument(doc);
  };

  const handleAddNewDocument = (newDoc: DocumentMetadata) => {
    setDocuments((prevDocs) => [newDoc, ...prevDocs]);
    setSelectedDocument(newDoc);
    // Refresh categories after adding a document
    fetchCategories();
  };

  const handleDeleteDocument = async (id: string) => {
    try {
      await apiService.deleteDocument(id);
      setDocuments((prevDocs) => prevDocs.filter((doc) => doc.id !== id));
      if (selectedDocument?.id === id) {
        setSelectedDocument(null);
      }
      // Refresh categories after deleting
      fetchCategories();
      toast({
        title: "Success",
        description: "Document deleted successfully",
      });
    } catch (error) {
      console.error("Failed to delete document:", error);
      toast({
        title: "Error",
        description: "Failed to delete document. Please try again.",
        variant: "destructive",
      });
    }
  };

  const filteredAndSortedDocuments = useMemo(() => {
    let filtered = documents;

    if (activeCategory !== "all") {
      const selectedCat = categories.find((c) => c.id === activeCategory);
      if (selectedCat) {
        filtered = filtered.filter((doc) =>
          doc.categories.includes(selectedCat.name)
        );
      }
    }

    const [key, order] = sortOption.split("-");

    return [...filtered].sort((a, b) => {
      let valA: any, valB: any;
      if (key === "title") {
        valA = (a.metadata?.title || a.fileName).toLowerCase();
        valB = (b.metadata?.title || b.fileName).toLowerCase();
      } else {
        // date
        valA = new Date(a.createdAtLocalDateTime || 0).getTime();
        valB = new Date(b.createdAtLocalDateTime || 0).getTime();
      }

      if (valA < valB) return order === "asc" ? -1 : 1;
      if (valA > valB) return order === "asc" ? 1 : -1;
      return 0;
    });
  }, [documents, activeCategory, sortOption]);

  return (
    <div className="flex h-screen w-full overflow-hidden bg-background text-foreground">
      <Sidebar
        activeCategory={activeCategory}
        setActiveCategory={setActiveCategory}
        categories={categories}
      />
      <main className="flex flex-1 flex-col overflow-hidden">
        <Header
          searchTerm={searchTerm}
          setSearchTerm={setSearchTerm}
          sortOption={sortOption}
          setSortOption={setSortOption}
          viewMode={viewMode}
          setViewMode={setViewMode}
          onUploadClick={() => setIsUploadOpen(true)}
        />
        <DocumentView
          documents={filteredAndSortedDocuments}
          selectedDocument={selectedDocument}
          onSelectDocument={handleSelectDocument}
          viewMode={viewMode}
          isLoading={isLoading}
        />
      </main>
      <AnimatePresence>
        {selectedDocument && (
          <MetadataSidebar
            document={selectedDocument}
            onClose={() => setSelectedDocument(null)}
            onDelete={() => handleDeleteDocument(selectedDocument.id)}
            onPreview={() => {
              // Preview functionality can be implemented here
              toast({
                title: "Preview",
                description: "Preview feature coming soon",
              });
            }}
          />
        )}
      </AnimatePresence>
      <UploadDialog
        open={isUploadOpen}
        onOpenChange={setIsUploadOpen}
        onUploadComplete={handleAddNewDocument}
      />
      <Toaster />
    </div>
  );
}

export default App;

