package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import session.UserSession;

public class LibraryRepository {
    public record TitleRow(String id, String name, String author, String genre, String isbn,
                           int totalCopies, int availableCopies) {}
    public record UserRow(String id, String name, String email, String role, double finesOwed) {}
    public record CopyRow(String id, String titleId, String title, String status, String condition) {}
    public record CheckoutRow(String id, String userId, String member, String copyId, String title,
                              LocalDateTime checkedOutAt, LocalDateTime dueAt, LocalDateTime returnedAt) {}
    public record HoldRow(String id, String userId, String member, String titleId, String title,
                          int queuePosition, String status, LocalDateTime placedAt, LocalDateTime expiresAt) {}
    public record FineRow(String id, String checkoutId, String userId, String member,
                          double amount, String status) {}
    public record AuditRow(String id, String userId, String userName, String action, String entityType,
                           String entityId, String notes, LocalDateTime createdAt) {}

    public List<TitleRow> findTitles(String search, String genre, String availability) throws SQLException {
        String sql = """
                SELECT t.id, t.name, t.author, t.genre, t.isbn,
                       COUNT(c.id) total_copies,
                       SUM(CASE WHEN c.status='available' THEN 1 ELSE 0 END) available_copies
                FROM title t LEFT JOIN copy c ON c.title_id=t.id
                WHERE (?='' OR LOWER(t.name) LIKE ? OR LOWER(t.author) LIKE ?)
                  AND (?='' OR t.genre=?)
                GROUP BY t.id,t.name,t.author,t.genre,t.isbn
                HAVING (?='' OR (?='available' AND available_copies>0)
                       OR (?='unavailable' AND available_copies=0))
                ORDER BY t.name
                """;
        String normalized = search == null ? "" : search.trim().toLowerCase();
        String selectedGenre = genre == null || genre.startsWith("All") ? "" : genre;
        String selectedAvailability = availability == null || availability.startsWith("All")
                ? "" : availability.toLowerCase();
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1, normalized); stmt.setString(2, "%" + normalized + "%");
            stmt.setString(3, "%" + normalized + "%"); stmt.setString(4, selectedGenre);
            stmt.setString(5, selectedGenre); stmt.setString(6, selectedAvailability);
            stmt.setString(7, selectedAvailability); stmt.setString(8, selectedAvailability);
            try (ResultSet rs = stmt.executeQuery()) {
                List<TitleRow> rows = new ArrayList<>();
                while (rs.next()) rows.add(new TitleRow(rs.getString("id"), rs.getString("name"),
                        rs.getString("author"), rs.getString("genre"), rs.getString("isbn"),
                        rs.getInt("total_copies"), rs.getInt("available_copies")));
                return rows;
            }
        }
    }

    public List<String> findGenres() throws SQLException {
        return strings("SELECT DISTINCT genre FROM title WHERE genre IS NOT NULL ORDER BY genre", "genre");
    }

    public TitleRow findTitleById(String id)throws SQLException{
        return findTitles("","","").stream().filter(row->row.id().equals(id)).findFirst().orElse(null);
    }

    public UserRow findUserById(String id)throws SQLException{
        return findUsers("").stream().filter(row->row.id().equals(id)).findFirst().orElse(null);
    }

    public List<CheckoutRow> findCheckoutsForTitle(String titleId)throws SQLException{
        return queryCheckouts("WHERE c.title_id='"+titleId.replace("'","''")+"' ORDER BY co.checked_out_at DESC","");
    }

    public List<HoldRow> findHoldsForTitle(String titleId)throws SQLException{
        return findHolds("").stream().filter(row->row.titleId().equals(titleId)).toList();
    }

    public int countTitles() throws SQLException { return count("SELECT COUNT(*) FROM title"); }
    public int countActiveCheckouts() throws SQLException { return count("SELECT COUNT(*) FROM checkout WHERE returned_at IS NULL"); }
    public List<CheckoutRow> findOverdueCheckouts() throws SQLException {
        return queryCheckouts("WHERE co.returned_at IS NULL AND co.due_at < NOW() ORDER BY co.due_at", "");
    }

    public List<UserRow> findUsers(String search) throws SQLException {
        String sql = "SELECT id,name,email,role,fines_owed FROM user "
                + "WHERE ?='' OR LOWER(name) LIKE ? OR LOWER(email) LIKE ? ORDER BY name";
        String value = search == null ? "" : search.trim().toLowerCase();
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1, value); stmt.setString(2, "%" + value + "%"); stmt.setString(3, "%" + value + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                List<UserRow> rows = new ArrayList<>();
                while (rs.next()) rows.add(new UserRow(rs.getString("id"), rs.getString("name"),
                        rs.getString("email"), rs.getString("role"), rs.getDouble("fines_owed")));
                return rows;
            }
        }
    }

    public List<CopyRow> availableCopies() throws SQLException {
        String sql = "SELECT c.id,c.title_id,t.name,c.status,c.condition FROM copy c "
                + "JOIN title t ON t.id=c.title_id WHERE c.status='available' ORDER BY t.name,c.id";
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            List<CopyRow> rows = new ArrayList<>();
            while (rs.next()) rows.add(new CopyRow(rs.getString("id"), rs.getString("title_id"),
                    rs.getString("name"), rs.getString("status"), rs.getString("condition")));
            return rows;
        }
    }

    public List<CheckoutRow> findCheckouts(String userId, boolean activeOnly) throws SQLException {
        String sql = "SELECT co.id,co.user_id,u.name AS member_name,co.copy_id,t.name AS title_name,"
                + "co.checked_out_at,co.due_at,co.returned_at FROM checkout co "
                + "JOIN user u ON u.id=co.user_id JOIN copy c ON c.id=co.copy_id JOIN title t ON t.id=c.title_id "
                + "WHERE (?='' OR co.user_id=?) AND (?=false OR co.returned_at IS NULL) ORDER BY co.checked_out_at DESC";
        try (PreparedStatement stmt = DBConn.getInstance().prepareStatement(sql)) {
            String id = userId == null ? "" : userId;
            stmt.setString(1, id); stmt.setString(2, id); stmt.setBoolean(3, activeOnly);
            try (ResultSet rs = stmt.executeQuery()) {
                List<CheckoutRow> rows = new ArrayList<>();
                while (rs.next()) rows.add(new CheckoutRow(rs.getString("id"), rs.getString("user_id"),
                        rs.getString("member_name"), rs.getString("copy_id"), rs.getString("title_name"),
                        time(rs,"checked_out_at"), time(rs,"due_at"), time(rs,"returned_at")));
                return rows;
            }
        }
    }

    public List<HoldRow> findHolds(String userId) throws SQLException {
        String sql="SELECT h.id,h.user_id,u.name AS member_name,h.title_id,t.name AS title_name,h.queue_position,h.status,h.placed_at,h.expires_at FROM hold h JOIN user u ON u.id=h.user_id JOIN title t ON t.id=h.title_id WHERE (?='' OR h.user_id=?) AND h.status<>'cancelled' ORDER BY h.placed_at DESC";
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql)){String id=userId==null?"":userId;stmt.setString(1,id);stmt.setString(2,id);try(ResultSet rs=stmt.executeQuery()){List<HoldRow> rows=new ArrayList<>();while(rs.next())rows.add(new HoldRow(rs.getString("id"),rs.getString("user_id"),rs.getString("member_name"),rs.getString("title_id"),rs.getString("title_name"),rs.getInt("queue_position"),rs.getString("status"),time(rs,"placed_at"),time(rs,"expires_at")));return rows;}}
    }

    public List<FineRow> findFines(String userId) throws SQLException {
        String sql="SELECT f.id,f.checkout_id,co.user_id,u.name AS member_name,f.amount,f.status FROM fine f JOIN checkout co ON co.id=f.checkout_id JOIN user u ON u.id=co.user_id WHERE (?='' OR co.user_id=?) ORDER BY f.id DESC";
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql)){String id=userId==null?"":userId;stmt.setString(1,id);stmt.setString(2,id);try(ResultSet rs=stmt.executeQuery()){List<FineRow> rows=new ArrayList<>();while(rs.next())rows.add(new FineRow(rs.getString("id"),rs.getString("checkout_id"),rs.getString("user_id"),rs.getString("member_name"),rs.getDouble("amount"),rs.getString("status")));return rows;}}
    }

    public List<AuditRow> findAuditRows() throws SQLException {
        String sql="SELECT a.id,a.user_id,u.name user_name,a.action,a.entity_type,a.entity_id,COALESCE(a.notes,'') notes,a.created_at FROM audit_log a LEFT JOIN user u ON u.id=a.user_id ORDER BY a.created_at DESC";
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql);ResultSet rs=stmt.executeQuery()){List<AuditRow> rows=new ArrayList<>();while(rs.next())rows.add(new AuditRow(rs.getString("id"),rs.getString("user_id"),rs.getString("user_name"),rs.getString("action"),rs.getString("entity_type"),rs.getString("entity_id"),rs.getString("notes"),time(rs,"created_at")));return rows;}
    }

    public void checkoutTitle(String userId, String titleId, LocalDateTime dueAt) throws SQLException {
        String copyId=null;
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement("SELECT id FROM copy WHERE title_id=? AND status='available' ORDER BY id LIMIT 1")){stmt.setString(1,titleId);try(ResultSet rs=stmt.executeQuery()){if(rs.next())copyId=rs.getString(1);}}
        if(copyId==null)throw new SQLException("No available copy remains for this title.");
        checkout(userId,copyId,dueAt);
    }

    public void checkout(String userId, String copyId, LocalDateTime dueAt) throws SQLException {
        Connection connection = DBConn.getInstance();
        boolean previousAutoCommit = connection.getAutoCommit();
        String checkoutId=nextId("checkout","co");
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement copy = connection.prepareStatement(
                    "UPDATE copy SET status='checked_out' WHERE id=? AND status='available'")) {
                copy.setString(1,copyId);
                if(copy.executeUpdate()!=1)throw new SQLException("That copy is no longer available.");
            }
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO checkout(id,user_id,copy_id,due_at) VALUES(?,?,?,?)")) {
                stmt.setString(1,checkoutId); stmt.setString(2,userId); stmt.setString(3,copyId);
                stmt.setTimestamp(4,Timestamp.valueOf(dueAt)); stmt.executeUpdate();
            }
            audit("CHECKOUT","checkout",checkoutId,"Book checked out to "+userName(userId));
            connection.commit();
        } catch (SQLException error) {
            connection.rollback();
            throw error;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    public void returnCheckout(String checkoutId) throws SQLException {
        Connection connection=DBConn.getInstance();
        boolean previousAutoCommit=connection.getAutoCommit();
        try{
            connection.setAutoCommit(false);
            String copyId=null;
            String titleId=null;
            try(PreparedStatement stmt=connection.prepareStatement(
                    "SELECT co.copy_id,c.title_id FROM checkout co JOIN copy c ON c.id=co.copy_id "
                            + "WHERE co.id=? AND co.returned_at IS NULL")){
                stmt.setString(1,checkoutId);
                try(ResultSet rs=stmt.executeQuery()){
                    if(rs.next()){copyId=rs.getString("copy_id");titleId=rs.getString("title_id");}
                }
            }
            if(copyId==null)throw new SQLException("The selected checkout is not active.");
            try(PreparedStatement stmt=connection.prepareStatement(
                    "UPDATE checkout SET returned_at=CURRENT_TIMESTAMP WHERE id=?")){
                stmt.setString(1,checkoutId);stmt.executeUpdate();
            }
            try(PreparedStatement stmt=connection.prepareStatement(
                    "UPDATE copy SET status='available' WHERE id=?")){
                stmt.setString(1,copyId);stmt.executeUpdate();
            }
            String holdId=null;
            try(PreparedStatement stmt=connection.prepareStatement(
                    "SELECT id FROM hold WHERE title_id=? AND status='waiting' "
                            + "ORDER BY queue_position LIMIT 1")){
                stmt.setString(1,titleId);
                try(ResultSet rs=stmt.executeQuery()){if(rs.next())holdId=rs.getString(1);}
            }
            if(holdId!=null){
                try(PreparedStatement stmt=connection.prepareStatement(
                        "UPDATE hold SET status='ready',expires_at=? WHERE id=?")){
                    stmt.setTimestamp(1,Timestamp.valueOf(LocalDateTime.now().plusDays(3)));
                    stmt.setString(2,holdId);stmt.executeUpdate();
                }
            }
            audit("RETURN","checkout",checkoutId,"Book returned");
            connection.commit();
        }catch(SQLException error){
            connection.rollback();
            throw error;
        }finally{
            connection.setAutoCommit(previousAutoCommit);
        }
    }
    public void cancelHold(String holdId) throws SQLException { update("UPDATE hold SET status='cancelled' WHERE id=?", holdId); audit("CANCEL","hold",holdId,"Hold cancelled"); }
    public void resolveFine(String fineId, String status) throws SQLException {
        Connection connection=DBConn.getInstance();
        boolean previousAutoCommit=connection.getAutoCommit();
        try{
            connection.setAutoCommit(false);
            String userId=null;
            String currentStatus=null;
            java.math.BigDecimal amount=null;
            try(PreparedStatement stmt=connection.prepareStatement(
                    "SELECT user_id,amount,status FROM fine WHERE id=?")){
                stmt.setString(1,fineId);
                try(ResultSet rs=stmt.executeQuery()){
                    if(rs.next()){
                        userId=rs.getString("user_id");amount=rs.getBigDecimal("amount");
                        currentStatus=rs.getString("status");
                    }
                }
            }
            if(userId==null)throw new SQLException("The selected fine no longer exists.");
            try(PreparedStatement stmt=connection.prepareStatement("UPDATE fine SET status=? WHERE id=?")){
                stmt.setString(1,status);stmt.setString(2,fineId);stmt.executeUpdate();
            }
            if("outstanding".equals(currentStatus)&&("paid".equals(status)||"waived".equals(status))){
                try(PreparedStatement stmt=connection.prepareStatement(
                        "UPDATE user SET fines_owed=CASE WHEN fines_owed>? THEN fines_owed-? ELSE 0 END WHERE id=?")){
                    stmt.setBigDecimal(1,amount);stmt.setBigDecimal(2,amount);stmt.setString(3,userId);stmt.executeUpdate();
                }
            }
            audit(status.toUpperCase(),"fine",fineId,"Fine marked "+status);
            connection.commit();
        }catch(SQLException error){
            connection.rollback();throw error;
        }finally{
            connection.setAutoCommit(previousAutoCommit);
        }
    }

    public void placeHold(String userId, String titleId) throws SQLException {
        String sql = "INSERT INTO hold(id,user_id,title_id,queue_position) SELECT ?,?,?,COALESCE(MAX(queue_position),0)+1 FROM hold WHERE title_id=? AND status='waiting'";
        String holdId=nextId("hold","h");
        try (PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql)) {
            stmt.setString(1,holdId); stmt.setString(2,userId); stmt.setString(3,titleId); stmt.setString(4,titleId); stmt.executeUpdate();
        }
        audit("CREATE","hold",holdId,"Hold placed for "+userId);
    }

    public void deleteTitle(String id) throws SQLException {
        Connection connection=DBConn.getInstance();
        int checkoutRecords;
        try(PreparedStatement stmt=connection.prepareStatement("SELECT COUNT(*) FROM checkout co JOIN copy c ON c.id=co.copy_id WHERE c.title_id=?")){
            stmt.setString(1,id);try(ResultSet rs=stmt.executeQuery()){rs.next();checkoutRecords=rs.getInt(1);}
        }
        if(checkoutRecords>0)throw new SQLException("This book cannot be deleted because borrowing records depend on it. Keep the title so checkout history remains accurate.");
        boolean previousAutoCommit=connection.getAutoCommit();
        try{
            connection.setAutoCommit(false);
            try(PreparedStatement holds=connection.prepareStatement("DELETE FROM hold WHERE title_id=?");PreparedStatement copies=connection.prepareStatement("DELETE FROM copy WHERE title_id=?");PreparedStatement title=connection.prepareStatement("DELETE FROM title WHERE id=?")){
                holds.setString(1,id);holds.executeUpdate();copies.setString(1,id);copies.executeUpdate();title.setString(1,id);title.executeUpdate();
            }
            connection.commit();
            audit("DELETE","title",id,"Book title and unreferenced copies deleted");
        }catch(SQLException error){connection.rollback();throw error;}finally{connection.setAutoCommit(previousAutoCommit);}
    }
    public void deleteUser(String id) throws SQLException { audit("DELETE","user",id,"Account deleted"); update("DELETE FROM user WHERE id=?", id); }

    public void createTitle(String name, String author, String genre, String isbn) throws SQLException {
        createTitle(name,author,genre,isbn,0);
    }
    public void createTitle(String name, String author, String genre, String isbn, int copyCount) throws SQLException {
        Connection connection = DBConn.getInstance();
        boolean previousAutoCommit = connection.getAutoCommit();
        String titleId = nextId("title","t");
        try {
            connection.setAutoCommit(false);
            try (PreparedStatement title = connection.prepareStatement(
                    "INSERT INTO title(id,name,author,genre,isbn) VALUES(?,?,?,?,?)")) {
                title.setString(1,titleId); title.setString(2,name); title.setString(3,author);
                title.setString(4,genre); title.setString(5,isbn); title.executeUpdate();
            }
            insertCopies(connection,titleId,copyCount);
            connection.commit();
            audit("CREATE","title",titleId,"Book created with "+copyCount+" copies");
        } catch (SQLException error) {
            connection.rollback();
            throw error;
        } finally {
            connection.setAutoCommit(previousAutoCommit);
        }
    }
    public void updateTitle(String id, String name, String author, String genre, String isbn) throws SQLException {
        execute("UPDATE title SET name=?,author=?,genre=?,isbn=? WHERE id=?", name, author, genre, isbn, id);
        audit("UPDATE","title",id,"Book details updated");
    }
    public void setTitleCopyCount(String titleId, int desiredCount) throws SQLException {
        Connection connection = DBConn.getInstance();
        int total;
        int unavailable;
        try (PreparedStatement stmt=connection.prepareStatement(
                "SELECT COUNT(*) total,SUM(CASE WHEN status<>'available' THEN 1 ELSE 0 END) unavailable FROM copy WHERE title_id=?")) {
            stmt.setString(1,titleId);
            try(ResultSet rs=stmt.executeQuery()){rs.next();total=rs.getInt("total");unavailable=rs.getInt("unavailable");}
        }
        if(desiredCount<unavailable)throw new SQLException("At least "+unavailable+" copies must remain because they are checked out, reserved, lost, or damaged.");
        if(desiredCount>total){insertCopies(connection,titleId,desiredCount-total);audit("UPDATE","title",titleId,"Copy count changed to "+desiredCount);return;}
        for(int i=0;i<total-desiredCount;i++){
            String copyId=null;
            try(PreparedStatement stmt=connection.prepareStatement(
                    "SELECT id FROM copy WHERE title_id=? AND status='available' ORDER BY id LIMIT 1")){
                stmt.setString(1,titleId);try(ResultSet rs=stmt.executeQuery()){if(rs.next())copyId=rs.getString(1);}
            }
            if(copyId!=null){
                try(PreparedStatement stmt=connection.prepareStatement("DELETE FROM copy WHERE id=?")){
                    stmt.setString(1,copyId);stmt.executeUpdate();
                }
            }
        }
        audit("UPDATE","title",titleId,"Copy count changed to "+desiredCount);
    }

    private void insertCopies(Connection connection,String titleId,int count)throws SQLException{
        try(PreparedStatement copy=connection.prepareStatement("INSERT INTO copy(id,title_id,status,`condition`) VALUES(?,?,'available','good')")){
            String firstId=nextId("copy","c");int firstNumber=Integer.parseInt(firstId.substring(2));
            for(int i=0;i<count;i++){copy.setString(1,String.format("c-%03d",firstNumber+i));copy.setString(2,titleId);copy.addBatch();}
            if(count>0)copy.executeBatch();
        }
    }
    public void createUser(String name, String email, String password) throws SQLException {
        createUser(name,email,password,"patron");
    }
    public void createUser(String name, String email, String password, String role) throws SQLException {
        if(!"patron".equals(role)&&!"librarian".equals(role))throw new SQLException("Account role must be patron or librarian.");
        String userId=nextId("user","u");
        execute("INSERT INTO user(id,name,email,password,role) VALUES(?,?,?,?,?)", userId, name, email, password, role);
        audit("CREATE","user",userId,("librarian".equals(role)?"Librarian":"Member")+" account created");
    }
    public void updateUser(String id, String name, String email) throws SQLException {
        execute("UPDATE user SET name=?,email=? WHERE id=?", name, email, id);
        audit("UPDATE","user",id,"Account profile updated");
    }
    public void updateUser(String id, String name, String email, String role) throws SQLException {
        if(!"patron".equals(role)&&!"librarian".equals(role))throw new SQLException("Account role must be patron or librarian.");
        execute("UPDATE user SET name=?,email=?,role=? WHERE id=?", name, email, role, id);
        audit("UPDATE","user",id,"Account details or role updated");
    }

    private void audit(String action,String entityType,String entityId,String message) throws SQLException {
        String actor=UserSession.isSignedIn()?UserSession.getCurrentUser().getUserID():null;
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement(
                "INSERT INTO audit_log(id,user_id,action,entity_type,entity_id,notes) VALUES(?,?,?,?,?,?)")){
            stmt.setString(1,nextId("audit_log","log"));stmt.setString(2,actor);stmt.setString(3,action);
            stmt.setString(4,entityType);stmt.setString(5,entityId);stmt.setString(6,message);stmt.executeUpdate();
        }
    }

    private String userName(String userId)throws SQLException{
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement("SELECT name FROM user WHERE id=?")){
            stmt.setString(1,userId);try(ResultSet rs=stmt.executeQuery()){return rs.next()?rs.getString(1):"Unknown member";}
        }
    }

    private void update(String sql, String id) throws SQLException {
        try (PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql)) { stmt.setString(1,id); stmt.executeUpdate(); }
    }
    private void execute(String sql, Object... values) throws SQLException {
        try (PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql)) {
            for (int i=0;i<values.length;i++) stmt.setObject(i+1,values[i]);
            stmt.executeUpdate();
        }
    }
    private String nextId(String table,String prefix)throws SQLException{
        int maximum=0;
        String marker=prefix+"-";
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement("SELECT id FROM `"+table+"`");
            ResultSet rs=stmt.executeQuery()){
            while(rs.next()){
                String id=rs.getString(1);
                if(id==null||!id.startsWith(marker))continue;
                try{maximum=Math.max(maximum,Integer.parseInt(id.substring(marker.length())));}
                catch(NumberFormatException ignored){}
            }
        }
        return String.format("%s-%03d",prefix,maximum+1);
    }
    private List<String> strings(String sql, String column) throws SQLException {
        try (PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql); ResultSet rs=stmt.executeQuery()) {
            List<String> values=new ArrayList<>(); while(rs.next()) values.add(rs.getString(column)); return values;
        }
    }
    private int count(String sql) throws SQLException { try(PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql);ResultSet rs=stmt.executeQuery()){rs.next();return rs.getInt(1);} }
    private List<CheckoutRow> queryCheckouts(String where, String ignored) throws SQLException {
        String sql="SELECT co.id,co.user_id,u.name AS member_name,co.copy_id,t.name AS title_name,co.checked_out_at,co.due_at,co.returned_at FROM checkout co JOIN user u ON u.id=co.user_id JOIN copy c ON c.id=co.copy_id JOIN title t ON t.id=c.title_id "+where;
        try(PreparedStatement stmt=DBConn.getInstance().prepareStatement(sql);ResultSet rs=stmt.executeQuery()){List<CheckoutRow> rows=new ArrayList<>();while(rs.next())rows.add(new CheckoutRow(rs.getString("id"),rs.getString("user_id"),rs.getString("member_name"),rs.getString("copy_id"),rs.getString("title_name"),time(rs,"checked_out_at"),time(rs,"due_at"),time(rs,"returned_at")));return rows;}
    }
    private LocalDateTime time(ResultSet rs, String column) throws SQLException {
        Timestamp value=rs.getTimestamp(column); return value==null?null:value.toLocalDateTime();
    }
}
