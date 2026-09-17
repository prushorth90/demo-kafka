import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { RefreshCw, Send } from 'lucide-react'
import './App.css'

const API_URL = 'http://localhost:8080/api/orders'

type OrderStatus = 'RECEIVED' | 'PROCESSING' | 'COMPLETED'

type Order = {
  orderId: string
  item: string
  quantity: number
  status: OrderStatus
}

async function requestOrders(): Promise<Order[]> {
  const response = await fetch(API_URL)

  if (!response.ok) {
    throw new Error('Could not load orders')
  }

  return response.json() as Promise<Order[]>
}

function App() {
  const [item, setItem] = useState('')
  const [quantity, setQuantity] = useState(1)
  const [orders, setOrders] = useState<Order[]>([])
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let isActive = true

    const loadOrders = async () => {
      try {
        const latestOrders = await requestOrders()
        if (isActive) {
          setOrders(latestOrders)
          setError(null)
        }
      } catch {
        if (isActive) {
          setError('The order service is unavailable. Check that Spring Boot is running.')
        }
      } finally {
        if (isActive) {
          setIsLoading(false)
        }
      }
    }

    void loadOrders()
    const pollingId = window.setInterval(loadOrders, 2_000)

    return () => {
      isActive = false
      window.clearInterval(pollingId)
    }
  }, [])

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setIsSubmitting(true)
    setError(null)

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ item: item.trim(), quantity }),
      })

      if (!response.ok) {
        throw new Error('Could not create order')
      }

      setItem('')
      setQuantity(1)
      setOrders(await requestOrders())
    } catch {
      setError('The order could not be placed. Check the details and try again.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="app-shell">
      <header className="page-header">
        <div>
          <p className="eyebrow">Kafka order pipeline</p>
          <h1>Order Demo</h1>
        </div>
        <div className={`live-indicator ${error ? 'is-offline' : ''}`} aria-live="polite">
          <span className="live-dot" />
          {error ? 'API offline' : 'Live feed'}
        </div>
      </header>

      <section className="order-form-section" aria-labelledby="new-order-title">
        <div className="section-heading">
          <span className="section-number">01</span>
          <h2 id="new-order-title">New order</h2>
        </div>

        <form className="order-form" onSubmit={handleSubmit}>
          <label className="field item-field">
            <span>Item</span>
            <input
              type="text"
              value={item}
              onChange={(event) => setItem(event.target.value)}
              placeholder="Burger"
              required
            />
          </label>

          <label className="field quantity-field">
            <span>Quantity</span>
            <input
              type="number"
              value={quantity}
              onChange={(event) => setQuantity(event.target.valueAsNumber)}
              min="1"
              required
            />
          </label>

          <button type="submit" disabled={isSubmitting || !item.trim()}>
            <Send size={18} aria-hidden="true" />
            {isSubmitting ? 'Placing...' : 'Place Order'}
          </button>
        </form>
      </section>

      {error && <p className="error-message" role="alert">{error}</p>}

      <section className="orders-section" aria-labelledby="orders-title">
        <div className="section-heading orders-heading">
          <div>
            <span className="section-number">02</span>
            <h2 id="orders-title">Orders</h2>
          </div>
          <RefreshCw className={isLoading ? 'is-spinning' : ''} size={18} aria-hidden="true" />
        </div>

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Order ID</th>
                <th>Item</th>
                <th>Quantity</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((order) => (
                <tr key={order.orderId}>
                  <td className="order-id" data-label="Order ID">{order.orderId}</td>
                  <td data-label="Item">{order.item}</td>
                  <td data-label="Quantity">{order.quantity}</td>
                  <td data-label="Status">
                    <span className={`status status-${order.status.toLowerCase()}`}>
                      {order.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {!isLoading && orders.length === 0 && !error && (
            <div className="empty-state">No orders yet</div>
          )}
          {isLoading && orders.length === 0 && (
            <div className="empty-state">Loading orders...</div>
          )}
        </div>
      </section>
    </main>
  )
}

export default App
