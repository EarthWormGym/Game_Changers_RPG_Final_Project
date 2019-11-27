require "spec_helper"
feature "sign up" do
  scenario "I should be able to visit the sign up page" do
    visit '/sign_up'
    expect(page).to have_content 'Sign Up for Battle!'
  end
end
