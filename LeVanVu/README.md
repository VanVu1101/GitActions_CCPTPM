# Simple Shop Backend (`LeVanVu`)

Backend Spring Boot dạng API-only cho web bán hàng đơn giản, sẵn sàng chạy bằng Docker.

## Tính năng

- API quản lý sản phẩm: xem danh sách, xem chi tiết, thêm, sửa, xóa
- API đơn hàng: tạo đơn hàng, xem danh sách, xem chi tiết
- Seed sẵn dữ liệu mẫu khi database trống
- Kết nối MySQL qua biến môi trường để phù hợp Docker Compose
- Có test backend dùng H2 in-memory
- Giao diện người dùng nằm ở folder `Fontend`, backend không còn render HTML

## API chính

### Sản phẩm
- `GET /api/products`
- `GET /api/products/{id}`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Đơn hàng
- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders`

### Ví dụ tạo đơn hàng

```json
{
  "customerName": "Nguyen Van A",
  "phone": "0900000000",
  "address": "123 Duong ABC, Ha Noi",
  "items": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

## Chạy local bằng Maven

```powershell
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

## Chạy bằng Docker Compose

Từ thư mục gốc `BaiTapDocker`:

```powershell
docker compose up --build
```

- Frontend: `http://localhost`
- Backend API: `http://localhost:8080/api/products`
- MySQL host port: `3307`
