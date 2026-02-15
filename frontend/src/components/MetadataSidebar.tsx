import { motion } from "framer-motion";
import { X, Download, Eye, Trash2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { FileIcon } from "@/components/file-icon";
import { formatDate, formatDateTime } from "@/lib/utils";
import type { DocumentMetadata } from "@/types";
import { apiService } from "@/services/api";
import { useToast } from "@/hooks/use-toast";

interface MetadataSidebarProps {
  document: DocumentMetadata;
  onClose: () => void;
  onPreview: () => void;
  onDelete: () => void;
}

export function MetadataSidebar({
  document,
  onClose,
  onPreview,
  onDelete,
}: MetadataSidebarProps) {
  const { toast } = useToast();

  const handleDownload = async () => {
    try {
      await apiService.downloadDocument(document.s3Key, document.fileName);
      toast({
        title: "Download started",
        description: `Downloading ${document.fileName}`,
      });
    } catch (error) {
      toast({
        title: "Download failed",
        description: "Could not download the document.",
        variant: "destructive",
      });
    }
  };

  return (
    <motion.aside
      initial={{ x: "100%" }}
      animate={{ x: "0%" }}
      exit={{ x: "100%" }}
      transition={{ type: "spring", stiffness: 300, damping: 30 }}
      className="hidden h-full w-96 shrink-0 flex-col border-l bg-card p-6 xl:flex overflow-y-auto"
    >
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-lg font-semibold">Document Details</h2>
        <Button variant="ghost" size="icon" onClick={onClose}>
          <X className="h-4 w-4" />
        </Button>
      </div>
      <Separator className="my-4" />
      <div className="flex flex-col items-center text-center mb-6">
        <div className="relative mb-4 flex h-24 w-24 items-center justify-center rounded-lg bg-muted">
          <FileIcon fileName={document.fileName} className="h-12 w-12 text-muted-foreground" />
        </div>
        <h3 className="font-semibold text-base">
          {document.metadata?.title || document.fileName}
        </h3>
        <p className="text-sm text-muted-foreground mt-1">{document.fileName}</p>
      </div>
      <Separator className="my-4" />
      <div className="space-y-4 text-sm">
        <div className="flex justify-between">
          <span className="text-muted-foreground">Author</span>
          <span className="text-right font-medium">
            {document.metadata?.authorOrSender || "N/A"}
          </span>
        </div>
        <div className="flex justify-between">
          <span className="text-muted-foreground">Date</span>
          <span>{document.metadata?.date || "N/A"}</span>
        </div>
        <div className="flex justify-between">
          <span className="text-muted-foreground">Added</span>
          <span>{formatDateTime(document.createdAtLocalDateTime)}</span>
        </div>
        <div className="flex justify-between items-center">
          <span className="text-muted-foreground">Categories</span>
            <div className="flex flex-wrap gap-1 justify-end">
              {(document.categories && document.categories.length > 0) ? (
                (document.categories || []).map((cat) => (
                  <Badge key={cat} variant="secondary" className="text-xs">
                    {cat}
                  </Badge>
                ))
              ) : (
                <span className="text-muted-foreground">None</span>
              )}
            </div>
        </div>
      </div>
      <Separator className="my-4" />
      <div>
        <h3 className="text-sm font-medium mb-2">Keywords</h3>
        <div className="flex flex-wrap gap-2">
          {(document.keywords && document.keywords.length > 0) ? (
            (document.keywords || []).map((keyword) => (
              <Badge key={keyword} variant="outline" className="text-xs">
                {keyword}
              </Badge>
            ))
          ) : (
            <span className="text-sm text-muted-foreground">No keywords</span>
          )}
        </div>
      </div>
      <Separator className="my-4" />
      <div>
        <h3 className="text-sm font-medium mb-2">Entities</h3>
        <div className="flex flex-wrap gap-2">
          {document.metadata?.entities && document.metadata.entities.length > 0 ? (
            document.metadata.entities.map((entity) => (
              <Badge key={entity} variant="outline" className="text-xs">
                {entity}
              </Badge>
            ))
          ) : (
            <span className="text-sm text-muted-foreground">No entities</span>
          )}
        </div>
      </div>
      <Separator className="my-4" />
      <div>
        <h3 className="text-sm font-medium mb-2">Summary</h3>
        <p className="text-sm text-muted-foreground leading-relaxed">
          {document.metadata?.summary || "No summary available."}
        </p>
      </div>
      {Object.keys(document.documentSpecificFields || {}).length > 0 && (
        <>
          <Separator className="my-4" />
          <div>
            <h3 className="text-sm font-medium mb-2">Additional Information</h3>
            <div className="space-y-2 text-sm">
              {Object.entries(document.documentSpecificFields || {}).map(([key, value]) => (
                <div className="flex justify-between" key={key}>
                  <span className="font-medium text-muted-foreground">{key}</span>
                  <span className="text-right">
                    {Array.isArray(value) ? value.join(", ") : String(value)}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </>
      )}
      <div className="mt-auto pt-6 space-y-2">
        <div className="flex gap-2">
          <Button variant="outline" className="flex-1" onClick={onPreview}>
            <Eye className="mr-2 h-4 w-4" /> Preview
          </Button>
          <Button className="flex-1" onClick={handleDownload}>
            <Download className="mr-2 h-4 w-4" /> Download
          </Button>
        </div>
        <Button variant="destructive" className="w-full" onClick={onDelete}>
          <Trash2 className="mr-2 h-4 w-4" /> Delete
        </Button>
      </div>
    </motion.aside>
  );
}

