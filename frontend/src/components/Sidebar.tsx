import { HardDrive, Folder, GanttChart } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { cn } from "@/lib/utils";

interface SidebarProps {
  activeCategory: string;
  setActiveCategory: (category: string) => void;
  categories: Array<{ id: string; name: string; documentCount: number }>;
}

export function Sidebar({
  activeCategory,
  setActiveCategory,
  categories,
}: SidebarProps) {
  return (
    <nav className="hidden h-full w-64 flex-col border-r bg-card p-4 md:flex">
      <div className="mb-6 flex items-center gap-2">
        <GanttChart className="h-7 w-7 text-primary" />
        <h1 className="text-xl font-bold text-foreground">DocFlow</h1>
      </div>
      <div className="flex-1 space-y-2">
        <h2 className="px-2 text-xs font-semibold text-muted-foreground uppercase tracking-wider">
          Main
        </h2>
        <Button
          variant={activeCategory === "all" ? "secondary" : "ghost"}
          className="w-full justify-start"
          onClick={() => setActiveCategory("all")}
        >
          <HardDrive className="mr-2 h-4 w-4" /> All Documents
        </Button>
        <h2 className="px-2 pt-4 text-xs font-semibold text-muted-foreground uppercase tracking-wider">
          Categories
        </h2>
        {categories.map((cat) => (
          <Button
            key={cat.id}
            variant={activeCategory === cat.id ? "secondary" : "ghost"}
            className="w-full justify-start"
            onClick={() => setActiveCategory(cat.id)}
          >
            <Folder className="mr-2 h-4 w-4" /> {cat.name}
            <span className="ml-auto text-xs text-muted-foreground">
              {cat.documentCount}
            </span>
          </Button>
        ))}
      </div>
      <div className="mt-auto">
        <Card className="shadow-none border-muted">
          <CardHeader className="p-4">
            <CardTitle className="text-sm">Storage Usage</CardTitle>
            <CardDescription className="text-xs">
              You are using 8.5 GB of 15 GB
            </CardDescription>
          </CardHeader>
          <CardContent className="p-4 pt-0">
            <Progress value={57} className="h-2 mb-4" />
            <Button size="sm" className="w-full" variant="outline">
              Upgrade Plan
            </Button>
          </CardContent>
        </Card>
      </div>
    </nav>
  );
}

