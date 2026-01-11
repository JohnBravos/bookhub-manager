export const LoadingScreen = ({message = "Loading..."}) => {
    return (
        <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-amber-50 to-orange-100">
            <div className="text-center">
                <div className="animate-spin rounded-full h-16 w-16 border-b-4 border-yellow-500 mx-auto mb-4"></div>
                <p className="text-xl text-gray-700 font-medium">{message}</p>
                <p className="text-sm text-gray-500 mt-2">Please wait...</p>
            </div>
        </div>
    );
}