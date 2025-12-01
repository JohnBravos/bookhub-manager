export default function ConfirmDialog({ 
  isOpen, 
  title = "Confirm Action", 
  message = "Are you sure?", 
  confirmText = "Delete",
  cancelText = "Cancel",
  onConfirm, 
  onCancel,
  isDangerous = true 
}) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg shadow-xl max-w-sm w-full mx-4 p-6">
        {/* Title */}
        <h2 className="text-lg font-bold text-[#3d2c1e] mb-2">
          {title}
        </h2>

        {/* Message */}
        <p className="text-[#5a4636] mb-6 text-sm">
          {message}
        </p>

        {/* Buttons */}
        <div className="flex gap-3 justify-end">
          <button
            onClick={onCancel}
            className="px-4 py-2 rounded-lg bg-gray-200 text-gray-800 font-semibold hover:bg-gray-300 transition"
          >
            {cancelText}
          </button>
          <button
            onClick={onConfirm}
            className={`px-4 py-2 rounded-lg text-white font-semibold transition ${
              isDangerous
                ? "bg-red-600 hover:bg-red-700"
                : "bg-[#8b5e34] hover:bg-[#704b29]"
            }`}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
