export default function DataTable({ columns, data, actions, emptyMessage = 'Aucune donnée' }) {
  if (!data || data.length === 0) {
    return <div className="alert alert-info">{emptyMessage}</div>
  }

  return (
    <div className="table-responsive bg-white rounded shadow-sm">
      <table className="table table-hover mb-0">
        <thead className="table-light">
          <tr>
            {columns.map((col) => (
              <th key={col.key}>{col.label}</th>
            ))}
            {actions && <th>Actions</th>}
          </tr>
        </thead>
        <tbody>
          {data.map((row, idx) => (
            <tr key={row.id || idx}>
              {columns.map((col) => (
                <td key={col.key}>
                  {col.render ? col.render(row) : row[col.key]}
                </td>
              ))}
              {actions && <td>{actions(row)}</td>}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
