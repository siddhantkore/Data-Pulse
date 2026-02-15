import { type ClassValue, clsx } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export function formatDate(dateString: string | undefined): string {
  if (!dateString) return "N/A";
  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
    });
  } catch {
    return dateString;
  }
}

export function formatDateTime(dateString: string | undefined): string {
  if (!dateString) return "N/A";
  try {
    const date = new Date(dateString);
    return date.toLocaleString("en-US", {
      year: "numeric",
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch {
    return dateString;
  }
}

export function getFileExtension(fileName: string): string {
  const parts = fileName.split(".");
  return parts.length > 1 ? parts[parts.length - 1].toLowerCase() : "file";
}

export function getFileTypeIcon(fileName: string): string {
  const ext = getFileExtension(fileName);
  const imageTypes = ["jpg", "jpeg", "png", "gif", "webp", "svg", "bmp"];
  const pdfTypes = ["pdf"];
  const wordTypes = ["doc", "docx"];
  const excelTypes = ["xls", "xlsx"];
  const textTypes = ["txt", "md", "rtf"];

  if (imageTypes.includes(ext)) return "image";
  if (pdfTypes.includes(ext)) return "pdf";
  if (wordTypes.includes(ext)) return "word";
  if (excelTypes.includes(ext)) return "excel";
  if (textTypes.includes(ext)) return "text";
  return "file";
}

