import { Search, UploadCloud, Settings, Grid, List, ChevronDown } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Separator } from "@/components/ui/separator";
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip";

interface HeaderProps {
  searchTerm: string;
  setSearchTerm: (term: string) => void;
  sortOption: string;
  setSortOption: (option: string) => void;
  viewMode: "grid" | "list";
  setViewMode: (mode: "grid" | "list") => void;
  onUploadClick: () => void;
}

const sortLabels: Record<string, string> = {
  "date-desc": "Date (Newest)",
  "date-asc": "Date (Oldest)",
  "title-asc": "Name (A-Z)",
  "title-desc": "Name (Z-A)",
};

export function Header({
  searchTerm,
  setSearchTerm,
  sortOption,
  setSortOption,
  viewMode,
  setViewMode,
  onUploadClick,
}: HeaderProps) {
  return (
    <TooltipProvider>
      <header className="flex h-16 shrink-0 items-center justify-between border-b px-6 bg-background">
        <div className="relative w-full max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
          <Input
            placeholder="Search documents..."
            className="pl-9"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex items-center gap-2">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="outline" className="w-48 justify-between">
                {sortLabels[sortOption] || "Sort by"} <ChevronDown className="h-4 w-4" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="w-48">
              <DropdownMenuLabel>Sort by</DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onSelect={() => setSortOption("date-desc")}>
                Date (Newest)
              </DropdownMenuItem>
              <DropdownMenuItem onSelect={() => setSortOption("date-asc")}>
                Date (Oldest)
              </DropdownMenuItem>
              <DropdownMenuItem onSelect={() => setSortOption("title-asc")}>
                Name (A-Z)
              </DropdownMenuItem>
              <DropdownMenuItem onSelect={() => setSortOption("title-desc")}>
                Name (Z-A)
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>

          <Tooltip>
            <TooltipTrigger asChild>
              <Button
                variant="outline"
                size="icon"
                onClick={() => setViewMode(viewMode === "grid" ? "list" : "grid")}
              >
                {viewMode === "grid" ? (
                  <List className="h-4 w-4" />
                ) : (
                  <Grid className="h-4 w-4" />
                )}
              </Button>
            </TooltipTrigger>
            <TooltipContent>
              <p>{viewMode === "grid" ? "Switch to List View" : "Switch to Grid View"}</p>
            </TooltipContent>
          </Tooltip>

          <Separator orientation="vertical" className="h-6" />

          <Button onClick={onUploadClick} className="bg-primary text-primary-foreground">
            <UploadCloud className="mr-2 h-4 w-4" /> Upload
          </Button>

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon">
                <Settings className="h-5 w-5" />
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end">
              <DropdownMenuItem>Account Settings</DropdownMenuItem>
              <DropdownMenuItem>Preferences</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem>Support</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem>Log out</DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </header>
    </TooltipProvider>
  );
}

