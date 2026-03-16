# Fontend
Frontend tĩnh cho web bán hàng đơn giản.
## Chức năng
- Hiển thị danh sách sản phẩm từ `GET /api/products`
- Thêm/xóa/chỉnh số lượng trong giỏ hàng
- Gửi đơn hàng tới `POST /api/orders`
- Chạy bằng Nginx trong Docker và proxy `/api` sang backend
## Chạy cùng Docker Compose
Từ thư mục gốc `BaiTapDocker`:
```powershell
docker compose up --build
```
- Frontend: `http://localhost`
- Backend API: `http://localhost:8080/api/products`
