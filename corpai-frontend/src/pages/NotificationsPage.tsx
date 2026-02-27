import { useEffect, useState } from 'react';
import { getNotifications } from '../services/api';
import type { Notification } from '../types';
import { BellIcon, ExclamationTriangleIcon } from '../components/Icons';

function priorityStyle(priority: string) {
  const map: Record<string, string> = {
    CRITICAL: 'border-l-4 border-red-500',
    HIGH: 'border-l-4 border-orange-400',
    MEDIUM: 'border-l-4 border-yellow-400',
    LOW: 'border-l-4 border-gray-300',
  };
  return map[priority] ?? '';
}

function priorityBadge(priority: string) {
  const map: Record<string, string> = {
    CRITICAL: 'bg-red-100 text-red-700',
    HIGH: 'bg-orange-100 text-orange-700',
    MEDIUM: 'bg-yellow-100 text-yellow-700',
    LOW: 'bg-gray-100 text-gray-500',
  };
  return map[priority] ?? 'bg-gray-100 text-gray-500';
}

export default function NotificationsPage() {
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    getNotifications()
      .then(setNotifications)
      .catch(() =>
        setError('Could not load notifications. Make sure the backend services are running.'),
      )
      .finally(() => setLoading(false));
  }, []);

  return (
    <div className="max-w-3xl mx-auto space-y-6">
      <div className="flex items-center gap-3">
        <BellIcon className="w-6 h-6 text-blue-700" />
        <h1 className="text-2xl font-bold text-gray-800">Advisor Notifications</h1>
      </div>

      {loading && (
        <div className="text-center text-gray-400 py-16 text-sm">Loading notifications…</div>
      )}

      {error && (
        <div className="bg-red-50 border border-red-200 rounded-xl p-5 text-red-700 text-sm flex gap-2">
          <ExclamationTriangleIcon className="w-5 h-5 shrink-0 mt-0.5" />
          {error}
        </div>
      )}

      {!loading && !error && notifications.length === 0 && (
        <div className="text-center text-gray-400 py-16">
          <BellIcon className="w-12 h-12 mx-auto mb-3 text-gray-200" />
          <p className="text-sm">No notifications at the moment.</p>
          <p className="text-xs text-gray-300 mt-1">
            Proactive alerts appear here once V3 monitoring is active.
          </p>
        </div>
      )}

      {notifications.map((n) => (
        <div
          key={n.id}
          className={`bg-white rounded-xl shadow-sm p-5 ${priorityStyle(n.priority)} ${
            n.read ? 'opacity-60' : ''
          }`}
        >
          <div className="flex items-start justify-between gap-2">
            <div>
              <p className="font-semibold text-gray-800 text-sm">{n.title}</p>
              {n.companyName && (
                <p className="text-xs text-gray-500 mt-0.5">
                  {n.companyName} · NIP {n.companyNip}
                </p>
              )}
            </div>
            <span
              className={`text-xs font-semibold px-2 py-0.5 rounded-full ${priorityBadge(n.priority)}`}
            >
              {n.priority}
            </span>
          </div>
          <p className="text-sm text-gray-600 mt-2">{n.message}</p>
          <p className="text-xs text-gray-400 mt-3">
            {new Date(n.createdAt).toLocaleString('pl-PL')}
          </p>
        </div>
      ))}
    </div>
  );
}
