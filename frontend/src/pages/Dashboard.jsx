export default function Dashboard() {
    return (
        <div className="p-6">
            <h1 className="text-3xl font-bold mb-4">Dashboard</h1>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                {/* Books */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h2 className="text-xl font-semibold mb-2">Books</h2>
                    <p className="text-gray-600">View and manage all books.</p>
                </div>

                {/* Loans */}
                <div className="bg-white rounded-lg shadow p-6">
                    <h2 className="text-xl font-semibold mb-2">Loans</h2>
                    <p className="text-gray-600">Track active and past loans.</p>
                </div>

                {/* Reservations */}
                <div className="bg-white rounded-lg shadow p-6">
                  <h2 className="text-xl font-semibold mb-2">Reservations</h2>
                  <p className="text-gray-600">Manage pending reservations.</p>
                </div>
            </div>
        </div>
    )
}