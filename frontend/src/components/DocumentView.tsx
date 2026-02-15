import { AnimatePresence, motion } from "framer-motion";
import { DocumentCard } from "./DocumentCard";
import { cn } from "@/lib/utils";
import type { DocumentMetadata } from "@/types";

interface DocumentViewProps {
  documents: DocumentMetadata[];
  selectedDocument: DocumentMetadata | null;
  onSelectDocument: (doc: DocumentMetadata) => void;
  viewMode: "grid" | "list";
  isLoading?: boolean;
}

export function DocumentView({
  documents,
  selectedDocument,
  onSelectDocument,
  viewMode,
  isLoading,
}: DocumentViewProps) {
  if (isLoading) {
    return (
      <div className="flex-1 overflow-y-auto p-8 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading documents...</p>
        </div>
      </div>
    );
  }

  if (documents.length === 0) {
    return (
      <div className="flex-1 overflow-y-auto p-8 flex items-center justify-center">
        <div className="text-center">
          <p className="text-lg font-semibold mb-2">No documents found</p>
          <p className="text-muted-foreground">
            Upload a document to get started
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex-1 overflow-y-auto p-8">
      <AnimatePresence>
        <motion.div
          key={viewMode}
          className={cn(
            "transition-all duration-300",
            viewMode === "grid"
              ? "grid grid-cols-2 gap-6 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5"
              : "flex flex-col gap-2"
          )}
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
        >
          {documents.map((doc, index) => (
            <DocumentCard
              key={doc.id}
              document={doc}
              isSelected={selectedDocument?.id === doc.id}
              viewMode={viewMode}
              onClick={() => onSelectDocument(doc)}
            />
          ))}
        </motion.div>
      </AnimatePresence>
    </div>
  );
}

