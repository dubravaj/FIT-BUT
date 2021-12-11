from fireprotdb.importer.db import Session, InterProEntry
from fireprotdb.importer.utils import data_file

session = Session()
entryfile = data_file("interpro/entry.list")

with open(entryfile) as fh:
    lines = fh.readlines()[1:]
    for line in lines:
        ac, type, name = line.strip().split("\t", 3)
        e = session.query(InterProEntry).filter(InterProEntry.interpro_entry_id == ac).first()
        if not e:
            session.add(InterProEntry(interpro_entry_id=ac, type=type, name=name))
        else:
            e.type = type
            e.name = name

    session.commit()