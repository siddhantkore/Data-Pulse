import {
  File,
  FileImage,
  FileText,
  Folder,
  Mail,
  Scan,
  UploadCloud,
  Cpu,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { getFileTypeIcon } from "@/lib/utils";

interface FileIconProps {
  fileName: string;
  className?: string;
}

export function FileIcon({ fileName, className }: FileIconProps) {
  const fileType = getFileTypeIcon(fileName);
  const baseClassName = "shrink-0";

  switch (fileType) {
    case "image":
      return <FileImage className={cn(baseClassName, className)} />;
    case "pdf":
      return <FileText className={cn(baseClassName, className)} />;
    case "word":
      return <FileText className={cn(baseClassName, className)} />;
    case "excel":
      return <FileText className={cn(baseClassName, className)} />;
    case "text":
      return <FileText className={cn(baseClassName, className)} />;
    default:
      return <File className={cn(baseClassName, className)} />;
  }
}

