# Document Management System - Frontend

A modern, professional React TypeScript frontend for the Spring Boot document management system.

## Features

- Modern, professional UI with Tailwind CSS and shadcn/ui components
- Responsive design with grid and list view modes
- Real-time search with debouncing
- Document upload with progress tracking
- Document metadata display with categories, keywords, and entities
- Fast and performant with React hooks and memoization
- Smooth animations with Framer Motion
- Pagination support for large document sets

## Tech Stack

- **React** - UI library
- **TypeScript** - Type safety
- **Vite** - Build tool and dev server
- **Tailwind CSS** - Styling
- **shadcn/ui** - UI component library
- **Lucide React** - Icons

## Getting Started

### Prerequisites

- Node.js 18+ and npm/yarn/pnpm
- Spring Boot backend running on `http://localhost:8080`

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm run dev
```

The app will be available at `http://localhost:3000`

### Build for Production

```bash
npm run build
```

The built files will be in the `dist` directory.

## Configuration

The API base URL can be configured via environment variables. Create a `.env` file:

```
VITE_API_BASE_URL=http://localhost:8080
```

If not set, it defaults to `http://localhost:8080`.

## Project Structure

```
frontend/
├── src/
│   ├── components/        # React components
│   │   ├── ui/           # shadcn/ui components
│   │   └── ...           # Feature components
│   ├── hooks/            # Custom React hooks
│   ├── lib/              # Utility functions
│   ├── services/         # API service layer
│   ├── types/            # TypeScript type definitions
│   ├── App.tsx           # Main application component
│   └── main.tsx          # Application entry point
├── public/               # Static assets
└── index.html            # HTML template
```

## API Integration

The frontend communicates with the Spring Boot backend through the following endpoints:

- `GET /api/search/get-all-docs` - Get paginated documents
- `GET /api/search/text-search?q=...` - Search documents
- `POST /api/documents/upload` - Upload a document
- `GET /api/documents/download/{s3Key}` - Download a document
- `GET /api/documents/pull` - Pull emails (not implemented in UI yet)

## Features

### Document Management
- View documents in grid or list mode
- Sort by date or title (ascending/descending)
- Filter by categories
- Search across document content

### Document Upload
- Drag and drop file upload
- Progress tracking
- File size validation (10MB max)
- Automatic document processing

### Document Details
- Full metadata display
- Categories and keywords
- Extracted entities
- Document summary
- Download functionality

## Development

The project uses:
- **ESLint** for code linting
- **TypeScript** for type checking
- **Vite** for fast HMR (Hot Module Replacement)

Run linting:
```bash
npm run lint
```


This project is part of the [Document Management System](https:github.com/siddhantkore/Data-Pulse).

