public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Object id) {
        super(resource + " ID " + id + "을(를) 찾을 수 없습니다.");
    }
}