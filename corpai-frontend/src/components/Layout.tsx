import { NavLink } from 'react-router-dom';
import {
  HomeIcon,
  MagnifyingGlassIcon,
  BellIcon,
} from './Icons';

export default function Layout({ children }: { children: React.ReactNode }) {
  const navItem =
    'flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-colors ' +
    'hover:bg-blue-700 [&.active]:bg-blue-700 text-white';

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      {/* Top bar */}
      <header className="bg-blue-800 text-white shadow-md">
        <div className="max-w-7xl mx-auto px-4 h-14 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <span className="text-xl font-bold tracking-wide">CorpAI</span>
            <span className="text-xs bg-blue-600 px-2 py-0.5 rounded-full font-semibold">
              PKO BP
            </span>
          </div>
          <nav className="flex items-center gap-1">
            <NavLink to="/" end className={navItem}>
              <HomeIcon className="w-4 h-4" />
              Dashboard
            </NavLink>
            <NavLink to="/analysis/new" className={navItem}>
              <MagnifyingGlassIcon className="w-4 h-4" />
              New Analysis
            </NavLink>
            <NavLink to="/notifications" className={navItem}>
              <BellIcon className="w-4 h-4" />
              Notifications
            </NavLink>
          </nav>
        </div>
      </header>

      {/* Main content */}
      <main className="flex-1 max-w-7xl w-full mx-auto px-4 py-8">
        {children}
      </main>

      <footer className="text-center text-xs text-gray-400 py-4 border-t border-gray-200">
        CorpAI Banking Intelligence Platform · PKO BP Corporate Banking
      </footer>
    </div>
  );
}
