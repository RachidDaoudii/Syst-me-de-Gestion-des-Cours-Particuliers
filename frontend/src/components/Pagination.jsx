export default function Pagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null

  const pages = []
  for (let i = 0; i < totalPages; i++) pages.push(i)

  return (
    <nav className="mt-3">
      <ul className="pagination justify-content-center">
        <li className={`page-item ${page === 0 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => onPageChange(page - 1)}>Précédent</button>
        </li>
        {pages.map((p) => (
          <li key={p} className={`page-item ${page === p ? 'active' : ''}`}>
            <button className="page-link" onClick={() => onPageChange(p)}>{p + 1}</button>
          </li>
        ))}
        <li className={`page-item ${page >= totalPages - 1 ? 'disabled' : ''}`}>
          <button className="page-link" onClick={() => onPageChange(page + 1)}>Suivant</button>
        </li>
      </ul>
    </nav>
  )
}
