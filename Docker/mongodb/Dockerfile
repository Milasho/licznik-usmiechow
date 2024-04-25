FROM mongo

# Initialize MongoDB with base dump
COPY mongo-backup /home/backups/mongo-backup

CMD mongod --fork --bind_ip 0.0.0.0 --logpath  /var/log/mongod.log \
    && mongorestore /home/backups/mongo-backup \
    && tail -F /var/log/mongod.log