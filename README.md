WebSocket:
 * Sử dụng nio -> server socket: SocketServerChannel, socket client: SocketChannel.
 * Khi mỗi kết nối được thiết lập, sau handshake, server sẽ lưu socket client dưới dạng [WebSocketImpl](WebSocketImpl) để giữ kết nối với client sãn sàng cho việc trao đổi dữ liệu.
 * Data sẽ được chia thành các [FrameData](code).
 * [FrameData](code) được chia thành 2 loại chính:
   * [DataFrame](code): Lưu trữ và vận chuyển dữ liệu
     * [TextFrame](code): Dữ liệu dạng text.
     * [BinaryFrame](code): Dữ liệu dạng nhị phân (dùng để chuyển file hoặc ảnh).
   * [ControlFrame](code): Dùng để điều khiển và giữ kết nối giữa client và server
     * [PingFrame](code): ping action
     * [PongFrame](code): pong action
     * [CloseFrame](code): Yêu cầu đóng kết nối.
     * [ContinousFrame](code): Ngoại trừ frame đầu và frame cuối là không cần gửi 