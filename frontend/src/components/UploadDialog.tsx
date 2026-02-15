import { useState, useEffect } from "react";
import { UploadCloud, AlertCircle } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Progress } from "@/components/ui/progress";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { apiService } from "@/services/api";
import { useToast } from "@/hooks/use-toast";
import type { DocumentMetadata } from "@/types";

interface UploadDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onUploadComplete: (document: DocumentMetadata) => void;
}

export function UploadDialog({
  open,
  onOpenChange,
  onUploadComplete,
}: UploadDialogProps) {
  const [file, setFile] = useState<File | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isUploading, setIsUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const { toast } = useToast();

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (selectedFile) {
      if (selectedFile.size > 10 * 1024 * 1024) {
        // 10MB limit (matching backend)
        setError("File is too large. Maximum size is 10MB.");
        setFile(null);
        toast({
          title: "Upload Error",
          description: "File is too large. Maximum size is 10MB.",
          variant: "destructive",
        });
      } else {
        setError(null);
        setFile(selectedFile);
      }
    }
  };

  const handleUpload = async () => {
    if (!file) return;

    setIsUploading(true);
    setProgress(0);

    const interval = setInterval(() => {
      setProgress((prev) => {
        if (prev >= 95) {
          return prev;
        }
        return prev + 5;
      });
    }, 200);

    try {
      const newDoc = await apiService.uploadDocument(file);
      clearInterval(interval);
      setProgress(100);

      setTimeout(() => {
        onUploadComplete(newDoc);
        setIsUploading(false);
        setFile(null);
        setProgress(0);
        onOpenChange(false);
        toast({
          title: "Upload successful",
          description: `${file.name} has been uploaded and processed.`,
        });
      }, 500);
    } catch (uploadError) {
      clearInterval(interval);
      setIsUploading(false);
      setError("Upload failed. Please try again.");
      toast({
        title: "Upload Error",
        description: "Could not upload the file. Please try again.",
        variant: "destructive",
      });
    }
  };

  useEffect(() => {
    if (!open) {
      setFile(null);
      setError(null);
      setIsUploading(false);
      setProgress(0);
    }
  }, [open]);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Upload Document</DialogTitle>
          <DialogDescription>
            Drag and drop a file or click to select. Max file size: 10MB.
          </DialogDescription>
        </DialogHeader>
        <div className="grid gap-4 py-4">
          {!isUploading && (
            <div className="relative flex h-32 w-full items-center justify-center rounded-lg border-2 border-dashed border-border">
              <div className="text-center">
                <UploadCloud className="mx-auto h-8 w-8 text-muted-foreground" />
                <p className="mt-2 text-sm text-muted-foreground">
                  {file ? file.name : "Drag & drop or click to select"}
                </p>
              </div>
              <Input
                id="file-upload"
                type="file"
                className="absolute inset-0 h-full w-full cursor-pointer opacity-0"
                onChange={handleFileChange}
              />
            </div>
          )}
          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertTitle>Error</AlertTitle>
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}
          {isUploading && (
            <div className="space-y-2">
              <p className="text-sm font-medium">Uploading {file?.name}...</p>
              <Progress value={progress} />
              <p className="text-right text-xs text-muted-foreground">
                {Math.round(progress)}%
              </p>
            </div>
          )}
        </div>
        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => onOpenChange(false)}
            disabled={isUploading}
          >
            Cancel
          </Button>
          <Button onClick={handleUpload} disabled={!file || isUploading}>
            {isUploading ? "Uploading..." : "Upload"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

