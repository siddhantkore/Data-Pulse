import { motion } from "framer-motion";
import { Card, CardContent, CardHeader } from "@/components/ui/card";
import { FileIcon } from "@/components/file-icon";
import { cn } from "@/lib/utils";
import type { DocumentMetadata } from "@/types";
import { formatDate } from "@/lib/utils";

interface DocumentCardProps {
  document: DocumentMetadata;
  isSelected: boolean;
  viewMode: "grid" | "list";
  onClick: () => void;
}

export function DocumentCard({
  document,
  isSelected,
  viewMode,
  onClick,
}: DocumentCardProps) {
  const title = document.metadata?.title || document.fileName;

  if (viewMode === "list") {
    return (
      <div
        className={cn(
          "flex items-center w-full p-3 rounded-lg cursor-pointer hover:bg-muted/50 transition-colors",
          isSelected && "bg-primary/10 border border-primary/20"
        )}
        onClick={onClick}
      >
        <div className="flex items-center gap-3 flex-1 min-w-0">
          <FileIcon fileName={document.fileName} className="h-6 w-6 text-muted-foreground flex-shrink-0" />
          <span className="font-medium truncate">{title}</span>
        </div>
        <div className="text-sm text-muted-foreground ml-4">
          {formatDate(document.createdAtLocalDateTime)}
        </div>
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2 }}
    >
      <Card
        className={cn(
          "w-full cursor-pointer transition-all duration-200 hover:shadow-md hover:-translate-y-1",
          isSelected && "ring-2 ring-primary shadow-lg"
        )}
        onClick={onClick}
      >
        <CardHeader className="p-4 pb-2">
          <div className="flex items-center justify-center">
            <FileIcon
              fileName={document.fileName}
              className="h-12 w-12 text-muted-foreground"
            />
          </div>
        </CardHeader>
        <CardContent className="p-4 pt-0">
          <h3 className="font-semibold truncate text-sm">
            {title}
          </h3>
          <p className="text-xs text-muted-foreground mt-1 truncate">
            {formatDate(document.createdAtLocalDateTime)}
          </p>
        </CardContent>
      </Card>
    </motion.div>
  );
}

