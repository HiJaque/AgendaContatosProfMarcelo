import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AgendaContatos extends JFrame {

    private JTextField txtNome, txtTelefone, txtEmail;
    private JTextArea txtListaContatos;

    private static final String DB_URL = "jdbc:sqlite:agenda_contatos.db"; // Banco SQLite

    public AgendaContatos() {
        setTitle("Agenda de Contatos");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        conectarBanco();

        JPanel panelInputs = new JPanel(new GridLayout(4, 2, 5, 5));
        panelInputs.add(new JLabel("Nome:"));
        txtNome = new JTextField();
        panelInputs.add(txtNome);

        panelInputs.add(new JLabel("Telefone:"));
        txtTelefone = new JTextField();
        panelInputs.add(txtTelefone);

        panelInputs.add(new JLabel("E-mail:"));
        txtEmail = new JTextField();
        panelInputs.add(txtEmail);

        JButton btnAdd = new JButton("Adicionar Contato");
        btnAdd.addActionListener(e -> adicionarContato());
        panelInputs.add(btnAdd);

        txtListaContatos = new JTextArea();
        txtListaContatos.setEditable(false);

        JButton btnClear = new JButton("Remover Todos");
        btnClear.addActionListener(e -> removerTodos());
        panelInputs.add(btnClear);

        add(panelInputs, BorderLayout.NORTH);
        add(new JScrollPane(txtListaContatos), BorderLayout.CENTER);

        atualizarListaContatos();
    }

    private void conectarBanco() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS contatos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nome TEXT NOT NULL, " +
                    "telefone TEXT NOT NULL, " +
                    "email TEXT NOT NULL)";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void adicionarContato() {
        String nome = txtNome.getText();
        String telefone = txtTelefone.getText();
        String email = txtEmail.getText();

        if (!nome.isEmpty() && !telefone.isEmpty() && !email.isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement("INSERT INTO contatos (nome, telefone, email) VALUES (?, ?, ?)")) {
                pstmt.setString(1, nome);
                pstmt.setString(2, telefone);
                pstmt.setString(3, email);
                pstmt.executeUpdate();
                atualizarListaContatos();
                limparCampos();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atualizarListaContatos() {
        txtListaContatos.setText("");
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM contatos")) {
            while (rs.next()) {
                txtListaContatos.append("Nome: " + rs.getString("nome") +
                        ", Telefone: " + rs.getString("telefone") +
                        ", E-mail: " + rs.getString("email") + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void removerTodos() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM contatos");
            atualizarListaContatos();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void limparCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AgendaContatos().setVisible(true));
    }
}
