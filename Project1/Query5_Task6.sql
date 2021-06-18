Select b1.Security, s.Description,r.NickName
From Skills s Natural JOIN (Select b.Security,hs.SkillId
From Accomplices a Natural JOIN Robbers r Natural JOIN HasSkills hs Natural JOIN Banks b) b1 Natural JOIN Robbers r JOIN HasSkills hs 
ON hs.SkillId = b1.SkillId AND r.RobberId = hs.RobberId
