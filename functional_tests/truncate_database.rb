require 'pg'

def clear_database
  connection = PG.connect(dbname: 'makersandmortals')

  # Clean the bookmarks table
  connection.exec("TRUNCATE players;")
end
