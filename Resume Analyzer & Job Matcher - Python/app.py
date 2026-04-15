import streamlit as st
import pdfplumber
import spacy
import re
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

nlp = spacy.load("en_core_web_sm")

SKILL_LIST = [
    "python", "java", "c++", "c", "sql", "mysql", "postgresql", "mongodb",
    "machine learning", "deep learning", "artificial intelligence", "nlp",
    "natural language processing", "computer vision", "data science",
    "pandas", "numpy", "matplotlib", "seaborn", "scikit-learn",
    "tensorflow", "pytorch", "keras", "streamlit", "flask", "django",
    "html", "css", "javascript", "react", "github", "git", "docker",
    "linux", "aws", "azure", "gcp", "api", "rest api", "communication",
    "problem solving", "leadership", "teamwork", "excel"
]

def extract_text_from_pdf(uploaded_file):
    text = ""
    with pdfplumber.open(uploaded_file) as pdf:
        for page in pdf.pages:
            text += (page.extract_text() or "") + "\n"
    return text

def calculate_similarity(resume_text, job_description):
    texts = [resume_text, job_description]
    vectorizer = TfidfVectorizer(stop_words="english")
    tfidf_matrix = vectorizer.fit_transform(texts)
    similarity = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:2])
    return round(similarity[0][0] * 100, 2)

def extract_skills(text, skill_list):
    text_lower = text.lower()
    found_skills = []

    for skill in skill_list:
        pattern = r"\b" + re.escape(skill.lower()) + r"\b"
        if re.search(pattern, text_lower):
            found_skills.append(skill)

    return sorted(set(found_skills))

def detect_sections(text):
    text_lower = text.lower()

    sections = {
        "skills": any(word in text_lower for word in ["skills", "technical skills"]),
        "experience": any(word in text_lower for word in ["experience", "work experience", "professional experience"]),
        "projects": any(word in text_lower for word in ["projects", "project", "academic projects"]),
        "education": "education" in text_lower,
        "certifications": "certifications" in text_lower
    }
    return sections

def section_feedback(sections, resume_text):
    feedback = []

    if sections["skills"]:
        feedback.append("Skills section found.")
    else:
        feedback.append("Add a clear Skills section.")

    if sections["experience"]:
        feedback.append("Experience section found.")
    else:
        feedback.append("Add an Experience section with internships, jobs, or training.")

    if sections["projects"]:
        feedback.append("Projects section found.")
    else:
        feedback.append("Add 1-2 relevant projects to strengthen your profile.")

    if sections["education"]:
        feedback.append("Education section found.")
    else:
        feedback.append("Add an Education section.")

    if sections["certifications"]:
        feedback.append("Certifications section found.")
    else:
        feedback.append("Add certifications if you have them.")

    # Check measurable impact
    if re.search(r"\b\d+%|\b\d+\b", resume_text):
        feedback.append("Good: Resume contains measurable details.")
    else:
        feedback.append("Add measurable impact like 'improved accuracy by 20%' or 'reduced time by 30%'.")

    return feedback

def improve_resume_suggestions(missing_skills, sections, job_description):
    suggestions = []

    if missing_skills:
        suggestions.append(f"Learn or mention missing skills: {', '.join(missing_skills[:5])}")

    job_lower = job_description.lower()

    if ("nlp" in job_lower or "natural language processing" in job_lower) and ("nlp" not in missing_skills):
        suggestions.append("Add projects related to NLP.")

    if ("machine learning" in job_lower or "ml" in job_lower) and ("machine learning" not in missing_skills):
        suggestions.append("Show ML projects with models, datasets, and results.")

    if not sections["projects"]:
        suggestions.append("Include a projects section with 1–3 relevant projects.")

    if not re.search(r"\b\d+%|\b\d+\b", job_description):
        suggestions.append("Use measurable impact in bullet points, such as percentages or counts.")

    suggestions.append("Use strong action verbs like built, developed, improved, optimized, and deployed.")

    return suggestions


st.set_page_config(page_title="Resume Analyzer", layout="centered")

st.title("Resume Analyzer")
st.write("Upload your resume PDF and paste a job description to analyze")

uploaded_file = st.file_uploader("Upload Resume (PDF)", type=["pdf"])
job_description = st.text_area("Paste Job Description Here", height=220)

if st.button("Analyze Resume"):
    if uploaded_file is None or job_description.strip() == "":
        st.warning("Please upload a resume and paste a job description.")
    else:
        with st.spinner("Analyzing resume..."):
            resume_text = extract_text_from_pdf(uploaded_file)

            if not resume_text.strip():
                st.error("Could not extract text from the PDF. Try another resume file.")
            else:
                score = calculate_similarity(resume_text, job_description)

                resume_skills = extract_skills(resume_text, SKILL_LIST)
                job_skills = extract_skills(job_description, SKILL_LIST)

                matched_skills = sorted(list(set(resume_skills) & set(job_skills)))
                missing_skills = sorted(list(set(job_skills) - set(resume_skills)))

                sections = detect_sections(resume_text)
                feedback = section_feedback(sections, resume_text)
                suggestions = improve_resume_suggestions(missing_skills, sections, job_description)

        st.success("Analysis Complete!")

        st.subheader("Score")
        st.metric(label="Resume Match %", value=f"{score}%")

        col1, col2 = st.columns(2)

        with col1:
            st.subheader("Matched Skills")
            if matched_skills:
                for skill in matched_skills:
                    st.write(f"- {skill}")
            else:
                st.write("No strong skill matches found.")

        with col2:
            st.subheader("Missing Skills")
            if missing_skills:
                for skill in missing_skills:
                    st.write(f"- {skill}")
            else:
                st.write("No missing skills detected.")

        st.subheader("Section-wise Feedback")
        for item in feedback:
            st.write(f"- {item}")

        st.subheader("Improve Resume Suggestions")
        for item in suggestions:
            st.write(f"- {item}")