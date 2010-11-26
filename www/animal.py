#!/usr/bin/python

"""
        Encapsulates functionality for the animal part of
        the database
"""

import db

def get_awaiting_vacc(dbo):
    """
    Returns a recordset of animals awaiting vaccinations
    """
    return db.query(dbo, "SELECT av.ID AS vaccid, a.ShelterCode, a.AnimalName, " +
        "av.DateRequired, av.Comments, vt.VaccinationType " +
        "FROM animal a " +
        "INNER JOIN animalvaccination av " +
        "ON a.ID = av.AnimalID " +
        "INNER JOIN vaccinationtype vt " +
        "ON vt.ID = av.VaccinationID " +
        "WHERE av.DateRequired Is Not Null AND av.DateOfVaccination Is Null " +
        "ORDER BY av.DateRequired, a.AnimalName")


