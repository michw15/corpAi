import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import DashboardPage from './pages/DashboardPage';
import NewAnalysisPage from './pages/NewAnalysisPage';
import AnalysisDetailPage from './pages/AnalysisDetailPage';
import NotificationsPage from './pages/NotificationsPage';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/analysis/new" element={<NewAnalysisPage />} />
          <Route path="/analysis/:correlationId" element={<AnalysisDetailPage />} />
          <Route path="/notifications" element={<NotificationsPage />} />
        </Routes>
      </Layout>
    </BrowserRouter>
  );
}

export default App;

