<?php
header("Content-Type: application/json; charset=UTF-8");
ini_set('display_errors', 1);
error_reporting(E_ALL);

class TeacherClassesFetcher {
    private $conn;

    public function __construct() {
        $this->conn = new mysqli("localhost", "root", "", "school1");
        if ($this->conn->connect_error) {
            $this->respondError("DB connection failed: " . $this->conn->connect_error);
        }
    }

    public function fetchClasses($teacherId) {
        if (!is_numeric($teacherId)) {
            $this->respondError("Invalid teacher ID");
        }

        $stmt = $this->conn->prepare("SELECT id, name FROM classes WHERE teacher_id = ?");
        $stmt->bind_param("i", $teacherId);
        $stmt->execute();
        $result = $stmt->get_result();

        $classes = [];
        while ($row = $result->fetch_assoc()) {
            $classes[] = $row;
        }

        $stmt->close();
        $this->conn->close();

        echo json_encode([
            "status" => "success",
            "data" => $classes
        ]);
    }

    private function respondError($message) {
        echo json_encode([
            "status" => "error",
            "message" => $message
        ]);
        exit;
    }
}

// 🟢 Run the class
if (isset($_GET['teacher_id'])) {
    $fetcher = new TeacherClassesFetcher();
    $fetcher->fetchClasses($_GET['teacher_id']);
} else {
    echo json_encode([
        "status" => "error",
        "message" => "Missing teacher_id parameter"
    ]);
}
?>
